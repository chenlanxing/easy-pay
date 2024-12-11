package com.lanxing.pay.core.wechat;

import com.lanxing.pay.core.NotifyUrl;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 直连微信支付基类
 *
 * @author chenlanxing
 */
@Slf4j
public abstract class DirectWechatPayService extends WechatPayService {

    protected <T> T getAmount(TransactionEntity transaction, Class<T> clazz) {
        try {
            T amount = clazz.getDeclaredConstructor().newInstance();
            Method setTotalMethod = clazz.getMethod("setTotal", Integer.class);
            setTotalMethod.invoke(amount, transaction.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
            Method setCurrencyMethod = clazz.getMethod("setCurrency", String.class);
            setCurrencyMethod.invoke(amount, "CNY");
            return amount;
        } catch (Exception e) {
            throw new PayException(e);
        }
    }

    protected <T> T getPrepayRequest(TransactionEntity transaction, WechatConfigEntity wechatConfig, Class<T> clazz) {
        try {
            T request = clazz.getDeclaredConstructor().newInstance();
            Method setMchidMethod = clazz.getMethod("setMchid", String.class);
            setMchidMethod.invoke(request, wechatConfig.getMchId());
            Method setAppidMethod = clazz.getMethod("setAppid", String.class);
            setAppidMethod.invoke(request, wechatConfig.getAppId());
            Method setOutTradeNoMethod = clazz.getMethod("setOutTradeNo", String.class);
            setOutTradeNoMethod.invoke(request, transaction.getTransactionNo());
            Method setDescriptionMethod = clazz.getMethod("setDescription", String.class);
            setDescriptionMethod.invoke(request, transaction.getDescription());
            Method setTimeExpireMethod = clazz.getMethod("setTimeExpire", String.class);
            setTimeExpireMethod.invoke(request, transaction.getExpireTime().format(FORMATTER));
            Method setNotifyUrlMethod = clazz.getMethod("setNotifyUrl", String.class);
            setNotifyUrlMethod.invoke(request, NotifyUrl.getPayNotifyUrl(transaction.getEntranceFlag()));
            return request;
        } catch (Exception e) {
            throw new PayException(e);
        }
    }

    protected boolean updateTransaction(TransactionEntity transaction, Transaction transactionResult) {
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

    @Override
    public TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag) {
        WechatConfigEntity wechatConfig = getWechatConfig(entranceFlag);
        RequestParam requestParam = getRequestParam(request);
        NotificationParser notificationParser = new NotificationParser(WechatPayFactory.getConfig(wechatConfig));
        Transaction transaction;
        try {
            transaction = notificationParser.parse(requestParam, Transaction.class);
        } catch (Exception e) {
            throw new PayException("解析支付通知失败", e);
        }
        String status = TransactionStatus.NOT_PAY;
        LocalDateTime finishTime = null;
        if (Transaction.TradeStateEnum.SUCCESS == transaction.getTradeState()) {
            status = TransactionStatus.SUCCESS;
            finishTime = LocalDateTime.parse(transaction.getSuccessTime(), FORMATTER);
        } else if (Transaction.TradeStateEnum.CLOSED == transaction.getTradeState()) {
            status = TransactionStatus.CLOSED;
        }
        return new TransactionEntity()
                .setTransactionNo(transaction.getOutTradeNo())
                .setStatus(status)
                .setOutTransactionNo(transaction.getTransactionId())
                .setFinishTime(finishTime);
    }
}
