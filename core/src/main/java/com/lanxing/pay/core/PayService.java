package com.lanxing.pay.core;

import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.entity.TransactionEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付接口
 *
 * @author chenlanxing
 */
public interface PayService {

    /**
     * 预支付
     * @param transaction .
     * @return .
     */
    Object prepay(TransactionEntity transaction);

    /**
     * 关闭支付
     * @param transaction .
     */
    void closePay(TransactionEntity transaction);

    /**
     * 查询支付
     * @param transaction .
     * @return .
     */
    boolean queryPay(TransactionEntity transaction);

    /**
     * 解析支付通知
     * @param request .
     * @param entranceFlag .
     * @return .
     */
    TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag);

    /**
     * 退款
     * @param transaction .
     * @param refund .
     */
    void refund(TransactionEntity transaction, RefundEntity refund);

    /**
     * 查询退款
     * @param transaction .
     * @param refund .
     * @return .
     */
    boolean queryRefund(TransactionEntity transaction, RefundEntity refund);

    /**
     * 解析退款通知
     * @param request .
     * @param entranceFlag .
     * @return .
     */
    RefundEntity parseRefundNotify(HttpServletRequest request, String entranceFlag);
}
