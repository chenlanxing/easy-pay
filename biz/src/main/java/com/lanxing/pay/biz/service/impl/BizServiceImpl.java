package com.lanxing.pay.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lanxing.pay.biz.constant.RedisLockConst;
import com.lanxing.pay.biz.exception.BizException;
import com.lanxing.pay.biz.model.req.PrepayReq;
import com.lanxing.pay.biz.model.req.RefundReq;
import com.lanxing.pay.biz.model.resp.PrepayResp;
import com.lanxing.pay.biz.model.resp.RefundResp;
import com.lanxing.pay.biz.model.resp.TransactionResp;
import com.lanxing.pay.biz.service.BizService;
import com.lanxing.pay.biz.util.IdUtil;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 业务实现
 *
 * @author chenlanxing
 */
@Slf4j
@Service
public class BizServiceImpl implements BizService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private PayService payService;

    @Autowired
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PrepayResp prepay(PrepayReq req) {
        log.info("预支付=>请求：{}", req);
        Assert.isTrue(LocalDateTime.now().isAfter(req.getExpireTime()), () -> new BizException("过期时间不能小于当前时间"));

        String lockKey = StrUtil.format(RedisLockConst.PREPAY_LOCK, req.getBizFlag(), req.getBizDataNo());
        RLock lock = redissonClient.getLock(lockKey);
        Assert.isTrue(lock.tryLock(),
                () -> new BizException("当前业务【{}:{}】正在执行预支付", req.getBizFlag(), req.getBizDataNo()));

        try {
            long successCount = transactionService.count(Wrappers.<TransactionEntity>lambdaQuery()
                    .eq(TransactionEntity::getBizFlag, req.getBizFlag())
                    .eq(TransactionEntity::getBizDataNo, req.getBizDataNo())
                    .eq(TransactionEntity::getStatus, TransactionStatus.SUCCESS));
            Assert.isTrue(successCount == 0,
                    () -> new BizException("当前业务【{}:{}】已支付成功", req.getBizFlag(), req.getBizDataNo()));

            String transactionNo = IdUtil.generate("100");
            TransactionEntity transaction = BeanUtil.copyProperties(req, TransactionEntity.class)
                    .setTransactionNo(transactionNo)
                    .setStatus(TransactionStatus.NOT_PAY);
            transactionService.save(transaction);
            log.info("预支付=>保存交易：{}", transaction);

            Object prepayInfo = payService.prepay(transaction);
            log.info("预支付=>结果：{}", prepayInfo);
            return new PrepayResp().setTransactionNo(transactionNo).setPrepayInfo(prepayInfo);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void closePay(String transactionNo) {
        log.info("关闭支付=>请求：{}", transactionNo);
        String lockKey = StrUtil.format(RedisLockConst.TRANSACTION_LOCK, transactionNo);
        RLock lock = redissonClient.getLock(lockKey);
        Assert.isTrue(lock.tryLock(), () -> new BizException("交易【{}】正在执行其他操作", transactionNo));

        try {
            TransactionEntity transaction = transactionService.getOne(Wrappers.<TransactionEntity>lambdaQuery()
                    .eq(TransactionEntity::getTransactionNo, transactionNo));
            Assert.notNull(transaction, () -> new BizException("交易不存在"));
            Assert.isTrue(TransactionStatus.NOT_PAY.equals(transaction.getStatus()),
                    () -> new BizException("交易【{}】已结束，无法关闭", transactionNo));

            transaction.setStatus(TransactionStatus.CLOSED);
            transactionService.updateById(transaction);
            log.info("关闭支付=>更新交易：{}", transaction);

            payService.closePay(transaction);
            log.info("关闭支付=>关闭操作已执行");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TransactionResp queryPay(String transactionNo) {
        log.info("查询支付=>请求：{}", transactionNo);
        String lockKey = StrUtil.format(RedisLockConst.TRANSACTION_LOCK, transactionNo);
        RLock lock = redissonClient.getLock(lockKey);
        Assert.isTrue(lock.tryLock(), () -> new BizException("交易【{}】正在执行其他操作", transactionNo));

        try {
            TransactionEntity transaction = transactionService.getOne(Wrappers.<TransactionEntity>lambdaQuery()
                    .eq(TransactionEntity::getTransactionNo, transactionNo));
            Assert.notNull(transaction, () -> new BizException("交易不存在"));

            if (TransactionStatus.NOT_PAY.equals(transaction.getStatus()) && payService.queryPay(transaction)) {
                transactionService.updateById(transaction);
                log.info("查询支付=>更新交易：{}", transaction);
            }

            TransactionResp resp = BeanUtil.copyProperties(transaction, TransactionResp.class);
            log.info("查询支付=>结果：{}", resp);
            return resp;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String refund(RefundReq req) {
        log.info("退款=>请求：{}", req);
        String lockKey = StrUtil.format(RedisLockConst.REFUND_REQUEST_LOCK, req.getTransactionNo());
        RLock lock = redissonClient.getLock(lockKey);
        Assert.isTrue(lock.tryLock(),
                () -> new BizException("交易【{}】正在执行退款", req.getTransactionNo()));

        try {
            TransactionEntity transaction = transactionService.getOne(Wrappers.<TransactionEntity>lambdaQuery()
                    .eq(TransactionEntity::getTransactionNo, req.getTransactionNo()));
            Assert.notNull(transaction, () -> new BizException("交易不存在"));
            Assert.isTrue(TransactionStatus.SUCCESS.equals(transaction.getStatus()),
                    () -> new BizException("交易【{}】未成功，无法退款", req.getTransactionNo()));

            long count = refundService.count(Wrappers.<RefundEntity>lambdaQuery()
                    .eq(RefundEntity::getBizFlag, req.getBizFlag())
                    .eq(RefundEntity::getBizDataNo, req.getBizDataNo())
                    .ne(RefundEntity::getStatus, RefundStatus.REFUND_FAIL));
            Assert.isTrue(count == 0, () -> new BizException("交易【{}】的退款业务【{}:{}】正在退款中或已退款成功",
                    req.getTransactionNo(), req.getBizFlag(), req.getBizDataNo()));

            String refundNo = IdUtil.generate("200");
            RefundEntity refund = BeanUtil.copyProperties(req, RefundEntity.class)
                    .setRefundNo(refundNo)
                    .setStatus(RefundStatus.REFUNDING);
            refundService.save(refund);
            log.info("退款=>保存退款：{}", refund);

            payService.refund(transaction, refund);
            log.info("退款=>退款操作已执行");
            return refundNo;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public RefundResp queryRefund(String refundNo) {
        log.info("查询退款=>请求：{}", refundNo);
        String lockKey = StrUtil.format(RedisLockConst.REFUND_LOCK, refundNo);
        RLock lock = redissonClient.getLock(lockKey);
        Assert.isTrue(lock.tryLock(), () -> new BizException("退款【{}】正在执行其他操作", refundNo));

        try {
            RefundEntity refund = refundService.getOne(Wrappers.<RefundEntity>lambdaQuery()
                    .eq(RefundEntity::getRefundNo, refundNo));
            Assert.notNull(refund, () -> new BizException("退款不存在"));
            TransactionEntity transaction = transactionService.getOne(Wrappers.<TransactionEntity>lambdaQuery()
                    .eq(TransactionEntity::getTransactionNo, refund.getTransactionNo()));

            if (RefundStatus.REFUNDING.equals(refund.getStatus()) && payService.queryRefund(transaction, refund)) {
                refundService.updateById(refund);
                log.info("查询退款=>更新退款：{}", refund);
            }

            RefundResp resp = BeanUtil.copyProperties(refund, RefundResp.class);
            log.info("查询退款=>结果：{}", resp);
            return resp;
        } finally {
            lock.unlock();
        }
    }
}
