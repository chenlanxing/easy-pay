package com.lanxing.pay.core.wechat;

import com.lanxing.pay.core.NotifyUrl;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.service.WechatConfigService;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.Amount;
import com.wechat.pay.java.service.payments.h5.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.h5.model.H5Info;
import com.wechat.pay.java.service.payments.h5.model.PrepayRequest;
import com.wechat.pay.java.service.payments.h5.model.PrepayResponse;
import com.wechat.pay.java.service.payments.h5.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.h5.model.SceneInfo;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 直连H5支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("wechatDirectH5")
public class DirectH5PayService extends DirectWechatPayService {

    @Autowired
    public void setWechatConfigService(WechatConfigService wechatConfigService) {
        this.wechatConfigService = wechatConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        H5Info h5Info = new H5Info();
        h5Info.setType("Wap");
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp(transaction.getUserIp());
        sceneInfo.setH5Info(h5Info);
        Amount amount = new Amount();
        amount.setTotal(transaction.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
        amount.setCurrency("CNY");
        PrepayRequest request = new PrepayRequest();
        request.setMchid(wechatConfig.getMchId());
        request.setAppid(wechatConfig.getAppId());
        request.setOutTradeNo(transaction.getTransactionNo());
        request.setDescription(transaction.getDescription());
        request.setTimeExpire(transaction.getExpireTime().format(FORMATTER));
        request.setNotifyUrl(NotifyUrl.getPayNotifyUrl(transaction.getEntranceFlag()));
        request.setAmount(amount);
        request.setSceneInfo(sceneInfo);
        H5Service h5Service = new H5Service.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        PrepayResponse response;
        try {
            response = h5Service.prepay(request);
        } catch (Exception e) {
            throw new PayException("关闭支付失败", e);
        }
        return response.getH5Url();
    }

    @Override
    public void closePay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        CloseOrderRequest request = new CloseOrderRequest();
        request.setMchid(wechatConfig.getMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        H5Service h5Service = new H5Service.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        try {
            h5Service.closeOrder(request);
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
        H5Service h5Service = new H5Service.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        Transaction transactionResult;
        try {
            transactionResult = h5Service.queryOrderByOutTradeNo(request);
        } catch (Exception e) {
            throw new PayException("查询支付失败", e);
        }
        return updateTransaction(transaction, transactionResult);
    }
}