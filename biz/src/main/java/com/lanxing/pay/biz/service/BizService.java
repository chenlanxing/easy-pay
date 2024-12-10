package com.lanxing.pay.biz.service;

import com.lanxing.pay.biz.model.req.PrepayReq;
import com.lanxing.pay.biz.model.req.RefundReq;
import com.lanxing.pay.biz.model.resp.PrepayResp;
import com.lanxing.pay.biz.model.resp.RefundResp;
import com.lanxing.pay.biz.model.resp.TransactionResp;

/**
 * 业务接口
 *
 * @author chenlanxing
 */
public interface BizService {

    /**
     * 预支付
     *
     * @param req .
     * @return .
     */
    PrepayResp prepay(PrepayReq req);

    /**
     * 关闭支付
     *
     * @param transactionNo .
     */
    void closePay(String transactionNo);

    /**
     * 查询支付
     *
     * @param transactionNo .
     * @return .
     */
    TransactionResp queryPay(String transactionNo);

    /**
     * 退款
     *
     * @param req .
     * @return .
     */
    String refund(RefundReq req);

    /**
     * 查询退款
     *
     * @param refundNo .
     * @return .
     */
    RefundResp queryRefund(String refundNo);
}
