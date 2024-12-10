package com.lanxing.pay.biz.controller;

import com.lanxing.pay.biz.model.APIResult;
import com.lanxing.pay.biz.model.req.PrepayReq;
import com.lanxing.pay.biz.model.req.RefundReq;
import com.lanxing.pay.biz.model.resp.PrepayResp;
import com.lanxing.pay.biz.model.resp.RefundResp;
import com.lanxing.pay.biz.model.resp.TransactionResp;
import com.lanxing.pay.biz.service.BizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 业务接口
 *
 * @author chenlanxing
 */
@RestController
public class BizController {

    @Autowired
    private BizService bizService;

    /**
     * 预支付
     *
     * @param req .
     * @return .
     */
    @PostMapping("/prepay")
    public APIResult prepay(@Valid @RequestBody PrepayReq req) {
        PrepayResp resp = bizService.prepay(req);
        return APIResult.success(resp);
    }

    /**
     * 关闭支付
     *
     * @param transactionNo .
     * @return .
     */
    @PostMapping("/closePay/{transactionNo}")
    public APIResult closePay(@PathVariable String transactionNo) {
        bizService.closePay(transactionNo);
        return APIResult.success();
    }

    /**
     * 查询支付
     *
     * @param transactionNo .
     * @return .
     */
    @PostMapping("/queryPay/{transactionNo}")
    public APIResult queryPay(@PathVariable String transactionNo) {
        TransactionResp resp = bizService.queryPay(transactionNo);
        return APIResult.success(resp);
    }

    /**
     * 退款
     *
     * @param req .
     * @return .
     */
    @PostMapping("/refund")
    public APIResult refund(@Valid @RequestBody RefundReq req) {
        String refundNo = bizService.refund(req);
        return APIResult.success(refundNo);
    }

    /**
     * 查询退款
     *
     * @param refundNo .
     * @return .
     */
    @PostMapping("/queryRefund/{refundNo}")
    public APIResult queryRefund(@PathVariable String refundNo) {
        RefundResp resp = bizService.queryRefund(refundNo);
        return APIResult.success(resp);
    }
}
