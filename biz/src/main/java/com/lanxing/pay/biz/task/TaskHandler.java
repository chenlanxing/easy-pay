package com.lanxing.pay.biz.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lanxing.pay.biz.constant.RedisLockConst;
import com.lanxing.pay.biz.model.resp.RefundResp;
import com.lanxing.pay.biz.model.resp.TransactionResp;
import com.lanxing.pay.core.PayService;
import com.lanxing.pay.data.constant.RefundStatus;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.service.RefundService;
import com.lanxing.pay.data.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 任务处理器
 *
 * @author chenlanxing
 */
@Slf4j
@Component
public class TaskHandler {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private PayService payService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier("callbackNotifyExecutor")
    private Executor callbackNotifyExecutor;

    /**
     * 处理未支付
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void handleNotPay() {
        LocalDateTime now = LocalDateTime.now();
        List<TransactionEntity> transactions = transactionService.list(Wrappers.<TransactionEntity>lambdaQuery()
                .eq(TransactionEntity::getStatus, TransactionStatus.NOT_PAY)
                .gt(TransactionEntity::getCreateTime, now.minusHours(2)));
        log.info("处理未支付的交易=>数量：{}", transactions.size());

        transactions.forEach(transaction -> {
            try {
                handleNotPay(transaction);
            } catch (Exception e) {
                log.warn("处理未支付的交易=>异常：{}", e.getMessage(), e);
            }
        });
    }

    private void handleNotPay(TransactionEntity transaction) {
        log.info("处理未支付的交易=>交易：{}", transaction);
        String lockKey = StrUtil.format(RedisLockConst.TRANSACTION_LOCK, transaction.getTransactionNo());
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
            return;
        }
        try {
            if (!payService.queryPay(transaction)) {
                return;
            }
            transactionService.updateById(transaction);
            log.info("处理未支付的交易=>更新交易：{}", transaction);

            if (StrUtil.isEmpty(transaction.getBizCallbackUrl())) {
                return;
            }

            CompletableFuture.runAsync(() -> {
                TransactionResp resp = BeanUtil.copyProperties(transaction, TransactionResp.class);
                String result = HttpUtil.post(transaction.getBizCallbackUrl(), JSON.toJSONString(resp));
                log.info("处理未支付的交易=>回调通知业务结果：{}", result);
            }, callbackNotifyExecutor);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 处理退款中
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void handleRefunding() {
        LocalDateTime now = LocalDateTime.now();
        List<RefundEntity> refunds = refundService.list(Wrappers.<RefundEntity>lambdaQuery()
                .eq(RefundEntity::getStatus, RefundStatus.REFUNDING)
                .gt(RefundEntity::getCreateTime, now.minusHours(2)));
        log.info("处理退款中的交易=>数量：{}", refunds.size());

        refunds.forEach(refund -> {
            try {
                handleRefunding(refund);
            } catch (Exception e) {
                log.warn("处理退款中的交易=>异常：{}", e.getMessage(), e);
            }
        });
    }

    private void handleRefunding(RefundEntity refund) {
        log.info("处理退款中的交易=>退款：{}", refund);
        String lockKey = StrUtil.format(RedisLockConst.REFUND_LOCK, refund.getRefundNo());
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
            return;
        }
        try {
            TransactionEntity transaction = transactionService.getOne(Wrappers.<TransactionEntity>lambdaQuery()
                    .eq(TransactionEntity::getTransactionNo, refund.getTransactionNo()));
            if (!payService.queryRefund(transaction, refund)) {
                return;
            }
            refundService.updateById(refund);
            log.info("处理退款中的交易=>更新退款：{}", refund);

            if (StrUtil.isEmpty(refund.getBizCallbackUrl())) {
                return;
            }

            CompletableFuture.runAsync(() -> {
                RefundResp resp = BeanUtil.copyProperties(refund, RefundResp.class);
                String result = HttpUtil.post(refund.getBizCallbackUrl(), JSON.toJSONString(resp));
                log.info("处理退款中的交易=>回调通知业务结果：{}", result);
            }, callbackNotifyExecutor);
        } finally {
            lock.unlock();
        }
    }
}