package com.lanxing.pay.core.wechat;

import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 直连微信支付基类
 *
 * @author chenlanxing
 */
@Slf4j
public abstract class DirectWechatPayService extends WechatPayService {

    protected <T> T getPrepayRequest(TransactionEntity transaction, WechatConfigEntity wechatConfig, Class<T> clazz) {
        try {
            T request = clazz.getDeclaredConstructor().newInstance();
            Method setMchidMethod = clazz.getMethod("setMchid", String.class);
            setMchidMethod.invoke(request, wechatConfig.getMchId());
            Method setAppidMethod = clazz.getMethod("setAppid", String.class);
            setAppidMethod.invoke(request, wechatConfig.getAppId());
            return getPrepayRequest(transaction, clazz, request);
        } catch (Exception e) {
            throw new PayException(e);
        }
    }

    protected boolean updateTransaction(TransactionEntity transaction, Transaction transactionResult) {
        TransactionEntity transactionNew = getTransaction(transactionResult, Transaction.class);
        transaction.setOutTransactionNo(transactionNew.getOutTransactionNo());
        transaction.setStatus(transactionNew.getStatus());
        transaction.setFinishTime(transactionNew.getFinishTime());
        return !TransactionStatus.NOT_PAY.equals(transactionNew.getStatus());
    }

    @Override
    public TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag) {
        return parsePayNotify(request, entranceFlag, Transaction.class);
    }
}
