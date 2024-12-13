package com.lanxing.pay.core;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lanxing.pay.data.entity.EntranceEntity;
import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.service.EntranceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认支付实现类
 *
 * @author chenlanxing
 */
@Slf4j
@Service
@Primary
public class DefaultPayService implements PayService {

    @Autowired
    private EntranceService entranceService;

    @Autowired
    private ApplicationContext applicationContext;

    private PayService select(String entranceFlag) {
        EntranceEntity entrance = entranceService.getOne(Wrappers.<EntranceEntity>lambdaQuery()
                .eq(EntranceEntity::getEntranceFlag, entranceFlag));
        Assert.notNull(entrance, () -> new PayException("入口标识未知"));
        Assert.isTrue(applicationContext.containsBean(entrance.getImplBeanName()), () -> new PayException("入口标识未知"));
        return applicationContext.getBean(entrance.getImplBeanName(), PayService.class);
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        return select(transaction.getEntranceFlag()).prepay(transaction);
    }

    @Override
    public void closePay(TransactionEntity transaction) {
        select(transaction.getEntranceFlag()).closePay(transaction);
    }

    @Override
    public boolean queryPay(TransactionEntity transaction) {
        return select(transaction.getEntranceFlag()).queryPay(transaction);
    }

    @Override
    public TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag) {
        return select(entranceFlag).parsePayNotify(request, entranceFlag);
    }

    @Override
    public void refund(TransactionEntity transaction, RefundEntity refund) {
        select(transaction.getEntranceFlag()).refund(transaction, refund);
    }

    @Override
    public boolean queryRefund(TransactionEntity transaction, RefundEntity refund) {
        return select(transaction.getEntranceFlag()).queryRefund(transaction, refund);
    }

    @Override
    public RefundEntity parseRefundNotify(HttpServletRequest request, String entranceFlag) {
        return select(entranceFlag).parseRefundNotify(request, entranceFlag);
    }
}
