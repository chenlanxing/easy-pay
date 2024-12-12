package com.lanxing.pay.core.wechat;

import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 服务商微信支付基类
 *
 * @author chenlanxing
 */
@Slf4j
public abstract class PartnerWechatPayService extends WechatPayService {

    protected <T> T getPrepayRequest(TransactionEntity transaction, WechatConfigEntity wechatConfig, Class<T> clazz) {
        try {
            T request = clazz.getDeclaredConstructor().newInstance();
            Method setSpMchidMethod = clazz.getMethod("setSpMchid", String.class);
            setSpMchidMethod.invoke(request, wechatConfig.getMchId());
            Method setSpAppidMethod = clazz.getMethod("setSpAppid", String.class);
            setSpAppidMethod.invoke(request, wechatConfig.getAppId());
            Method setSubMchidMethod = clazz.getMethod("setSubMchid", String.class);
            setSubMchidMethod.invoke(request, wechatConfig.getSubMchId());
            return getPrepayRequest(transaction, clazz, request);
        } catch (Exception e) {
            throw new PayException(e);
        }
    }

    protected <T> boolean updateTransaction(TransactionEntity transaction, T transactionResult, Class<T> clazz) {
        TransactionEntity transactionNew = getTransaction(transactionResult, clazz);
        transaction.setOutTransactionNo(transactionNew.getOutTransactionNo());
        transaction.setStatus(transactionNew.getStatus());
        transaction.setFinishTime(transactionNew.getFinishTime());
        return !TransactionStatus.NOT_PAY.equals(transactionNew.getStatus());
    }
}
