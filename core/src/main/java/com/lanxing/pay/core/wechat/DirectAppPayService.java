package com.lanxing.pay.core.wechat;

import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.lanxing.pay.core.NotifyUrl;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.service.WechatConfigService;
import com.wechat.pay.java.core.http.Constant;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.GsonUtil;
import com.wechat.pay.java.service.payments.app.AppServiceExtension;
import com.wechat.pay.java.service.payments.app.model.Amount;
import com.wechat.pay.java.service.payments.app.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.app.model.PrepayRequest;
import com.wechat.pay.java.service.payments.app.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.app.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 直连APP支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("wechatDirectApp")
public class DirectAppPayService extends DirectWechatPayService {

    @Autowired
    public void setWechatConfigService(WechatConfigService wechatConfigService) {
        this.wechatConfigService = wechatConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
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
        AppServiceExtension appService = new AppServiceExtension.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        PrepayWithRequestPaymentResponse response;
        try {
             response = appService.prepayWithRequestPayment(request);
        } catch (Exception e) {
            throw new PayException("关闭支付失败", e);
        }
        return JSON.parseObject(GsonUtil.toJson(response));
    }

    @Override
    public void closePay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        CloseOrderRequest request = new CloseOrderRequest();
        request.setMchid(wechatConfig.getMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        AppServiceExtension appService = new AppServiceExtension.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        try {
            appService.closeOrder(request);
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
        AppServiceExtension appService = new AppServiceExtension.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        Transaction transactionResult;
        try {
            transactionResult = appService.queryOrderByOutTradeNo(request);
        } catch (Exception e) {
            throw new PayException("查询支付失败", e);
        }
        transaction.setOutTransactionNo(transactionResult.getTransactionId());
        if (Transaction.TradeStateEnum.SUCCESS == transactionResult.getTradeState()) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setFinishTime(LocalDateTime.parse(transactionResult.getSuccessTime(), FORMATTER));
            return true;
        } else if (Transaction.TradeStateEnum.CLOSED == transactionResult.getTradeState()) {
            transaction.setStatus(TransactionStatus.CLOSED);
            return false;
        }
        return false;
    }
}
