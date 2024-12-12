package com.lanxing.pay.core.wechat;

import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.service.WechatConfigService;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 直连Native支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("wechatDirectNative")
public class DirectNativePayService extends DirectWechatPayService {

    @Autowired
    public void setWechatConfigService(WechatConfigService wechatConfigService) {
        this.wechatConfigService = wechatConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        PrepayRequest request = getPrepayRequest(transaction, wechatConfig, PrepayRequest.class);
        request.setAmount(getAmount(transaction, Amount.class));
        NativePayService nativePayService = new NativePayService.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        PrepayResponse response;
        try {
            response = nativePayService.prepay(request);
        } catch (Exception e) {
            throw new PayException("预支付失败", e);
        }
        return response.getCodeUrl();
    }

    @Override
    public void closePay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        CloseOrderRequest request = new CloseOrderRequest();
        request.setMchid(wechatConfig.getMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        NativePayService nativePayService = new NativePayService.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        try {
            nativePayService.closeOrder(request);
        } catch (Exception e) {
            throw new PayException("关闭支付失败", e);
        }
    }

    @Override
    public boolean queryPay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setMchid(wechatConfig.getMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        NativePayService nativePayService = new NativePayService.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        Transaction transactionResult;
        try {
            transactionResult = nativePayService.queryOrderByOutTradeNo(request);
        } catch (Exception e) {
            throw new PayException("查询支付失败", e);
        }
        return updateTransaction(transaction, transactionResult);
    }
}
