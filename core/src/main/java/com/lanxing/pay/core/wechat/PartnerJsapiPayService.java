package com.lanxing.pay.core.wechat;

import com.alibaba.fastjson.JSON;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.service.WechatConfigService;
import com.lanxing.pay.data.service.WechatUserService;
import com.wechat.pay.java.core.util.GsonUtil;
import com.wechat.pay.java.service.partnerpayments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Amount;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Payer;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务商JSAPI支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("wechatPartnerJsapi")
public class PartnerJsapiPayService extends PartnerWechatPayService {

    @Autowired
    private WechatUserService wechatUserService;

    @Autowired
    public void setWechatConfigService(WechatConfigService wechatConfigService) {
        this.wechatConfigService = wechatConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        Payer payer = new Payer();
        payer.setSpOpenid(getOpenId(wechatUserService, transaction, wechatConfig));
        PrepayRequest request = getPrepayRequest(transaction, wechatConfig, PrepayRequest.class);
        request.setAmount(getAmount(transaction, Amount.class));
        request.setPayer(payer);
        JsapiServiceExtension jsapiService = new JsapiServiceExtension.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        PrepayWithRequestPaymentResponse response;
        try {
            response = jsapiService.prepayWithRequestPayment(request, wechatConfig.getAppId());
        } catch (Exception e) {
            throw new PayException("预支付失败", e);
        }
        return JSON.parseObject(GsonUtil.toJson(response));
    }

    @Override
    public void closePay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        CloseOrderRequest request = new CloseOrderRequest();
        request.setSpMchid(wechatConfig.getMchId());
        request.setSubMchid(wechatConfig.getSubMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        JsapiServiceExtension jsapiService = new JsapiServiceExtension.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        try {
            jsapiService.closeOrder(request);
        } catch (Exception e) {
            throw new PayException("关闭支付失败", e);
        }
    }

    @Override
    public boolean queryPay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setSpMchid(wechatConfig.getMchId());
        request.setSubMchid(wechatConfig.getSubMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        JsapiServiceExtension jsapiService = new JsapiServiceExtension.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        Transaction transactionResult;
        try {
            transactionResult = jsapiService.queryOrderByOutTradeNo(request);
        } catch (Exception e) {
            throw new PayException("查询支付失败", e);
        }
        return updateTransaction(transaction, transactionResult, Transaction.class);
    }

    @Override
    public TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag) {
        return parsePayNotify(request, entranceFlag, Transaction.class);
    }
}
