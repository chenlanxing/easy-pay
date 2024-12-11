package com.lanxing.pay.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lanxing.pay.biz.constant.RedisLockConst;
import com.lanxing.pay.biz.common.exception.BizException;
import com.lanxing.pay.biz.model.resp.RefundResp;
import com.lanxing.pay.biz.model.resp.TransactionResp;
import com.lanxing.pay.biz.service.CallbackService;
import com.lanxing.pay.core.PayService;
import com.lanxing.pay.data.entity.EntranceEntity;
import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.service.EntranceService;
import com.lanxing.pay.data.service.RefundService;
import com.lanxing.pay.data.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 回调业务实现
 *
 * @author chenlanxing
 */
@Slf4j
@Service
public class CallbackServiceImpl implements CallbackService {

    @Autowired
    private EntranceService entranceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private PayService payService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier("callbackExecutor")
    private Executor callbackExecutor;

    @Autowired
    @Qualifier("callbackNotifyExecutor")
    private Executor callbackNotifyExecutor;

    @Override
    public String payNotify(String entranceFlag, HttpServletRequest request) {
        EntranceEntity entrance = entranceService.getOne(Wrappers.<EntranceEntity>lambdaQuery()
                .eq(EntranceEntity::getEntranceFlag, entranceFlag));
        Assert.notNull(entrance, () -> new BizException("入口标识未知"));

        CompletableFuture.runAsync(() -> {
            TransactionEntity parse = payService.parsePayNotify(request, entranceFlag);
            String lockKey = StrUtil.format(RedisLockConst.TRANSACTION_LOCK, parse.getTransactionNo());
            RLock lock = redissonClient.getLock(lockKey);
            if (!lock.tryLock()) {
                return;
            }
            try {
                TransactionEntity transaction = transactionService.getOne(Wrappers.<TransactionEntity>lambdaQuery()
                        .eq(TransactionEntity::getTransactionNo, parse.getTransactionNo()));
                transaction.setStatus(parse.getStatus())
                        .setFinishTime(parse.getFinishTime())
                        .setOutTransactionNo(parse.getOutTransactionNo());
                transactionService.updateById(transaction);
                if (StrUtil.isNotEmpty(transaction.getBizCallbackUrl())) {
                    CompletableFuture.runAsync(
                            () -> HttpUtil.post(transaction.getBizCallbackUrl(), JSON.toJSONString(BeanUtil.copyProperties(transaction, TransactionResp.class))),
                            callbackNotifyExecutor
                    );
                }
            } finally {
                lock.unlock();
            }
        }, callbackExecutor);
        return entrance.getDefaultCallbackData();
    }

    @Override
    public String refundNotify(String entranceFlag, HttpServletRequest request) {
        EntranceEntity entrance = entranceService.getOne(Wrappers.<EntranceEntity>lambdaQuery()
                .eq(EntranceEntity::getEntranceFlag, entranceFlag));
        Assert.notNull(entrance, () -> new BizException("入口标识未知"));

        CompletableFuture.runAsync(() -> {
            RefundEntity parse = payService.parseRefundNotify(request, entranceFlag);
            String lockKey = StrUtil.format(RedisLockConst.REFUND_LOCK, parse.getRefundNo());
            RLock lock = redissonClient.getLock(lockKey);
            if (!lock.tryLock()) {
                return;
            }
            try {
                RefundEntity refund = refundService.getOne(Wrappers.<RefundEntity>lambdaQuery()
                        .eq(RefundEntity::getRefundNo, parse.getRefundNo()));
                refund.setStatus(parse.getStatus())
                        .setFinishTime(parse.getFinishTime())
                        .setOutRefundNo(parse.getOutRefundNo());
                refundService.updateById(refund);
                if (StrUtil.isNotEmpty(refund.getBizCallbackUrl())) {
                    CompletableFuture.runAsync(
                            () -> HttpUtil.post(refund.getBizCallbackUrl(), JSON.toJSONString(BeanUtil.copyProperties(refund, RefundResp.class))),
                            callbackNotifyExecutor
                    );
                }
            } finally {
                lock.unlock();
            }
        }, callbackExecutor);
        return entrance.getDefaultCallbackData();
    }
}
