package com.lanxing.pay.core.wechat;

import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 直连微信支付基类
 *
 * @author chenlanxing
 */
@Slf4j
public abstract class DirectWechatPayService extends WechatPayService {

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
