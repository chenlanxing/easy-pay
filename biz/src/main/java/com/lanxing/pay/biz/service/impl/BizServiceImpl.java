package com.lanxing.pay.biz.service.impl;

import com.lanxing.pay.biz.model.req.PrepayReq;
import com.lanxing.pay.biz.model.req.RefundReq;
import com.lanxing.pay.biz.model.resp.PrepayResp;
import com.lanxing.pay.biz.model.resp.RefundResp;
import com.lanxing.pay.biz.model.resp.TransactionResp;
import com.lanxing.pay.biz.service.BizService;
import com.lanxing.pay.core.PayService;
import com.lanxing.pay.data.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private PayService payService;

    @Override
    public PrepayResp prepay(PrepayReq req) {
        return null;
    }

    @Override
    public void closePay(String transactionNo) {

    }

    @Override
    public TransactionResp queryPay(String transactionNo) {
        return null;
    }

    @Override
    public String refund(RefundReq req) {
        return "";
    }

    @Override
    public RefundResp queryRefund(String refundNo) {
        return null;
    }
}
