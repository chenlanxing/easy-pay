package com.lanxing.pay.core.wechat;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lanxing.pay.core.NotifyUrl;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.core.PayService;
import com.lanxing.pay.data.constant.RefundStatus;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.entity.WechatUserEntity;
import com.lanxing.pay.data.service.WechatConfigService;
import com.lanxing.pay.data.service.WechatUserService;
import com.wechat.pay.java.core.http.Constant;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.RefundNotification;
import com.wechat.pay.java.service.refund.model.Status;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 微信支付基类
 *
 * @author chenlanxing
 */
@Slf4j
public abstract class WechatPayService implements PayService {

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DatePattern.UTC_WITH_XXX_OFFSET_PATTERN);

    protected WechatConfigService wechatConfigService;

    protected WechatConfigEntity getWechatConfig(String entranceFlag) {
        WechatConfigEntity wechatConfig = wechatConfigService.getOne(Wrappers.<WechatConfigEntity>lambdaQuery()
                .eq(WechatConfigEntity::getEntranceFlag, entranceFlag));
        Assert.notNull(wechatConfig, () -> new PayException("微信配置不存在"));
        return wechatConfig;
    }

    protected String getOpenId(WechatUserService wechatUserService, TransactionEntity transaction, WechatConfigEntity wechatConfig) {
        WechatUserEntity wechatUser = wechatUserService.getOne(Wrappers.<WechatUserEntity>lambdaQuery()
                .eq(WechatUserEntity::getUserId, transaction.getUserId())
                .eq(WechatUserEntity::getAppId, wechatConfig.getAppId()));
        Assert.notNull(wechatConfig, () -> new PayException("微信用户不存在"));
        return wechatUser.getOpenId();
    }

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

    protected <A, B> A getSceneInfo(TransactionEntity transaction, Class<A> aClass, Class<B> bClass) {
        try {
            B h5Info = bClass.getDeclaredConstructor().newInstance();
            Method setTypeMethod = bClass.getMethod("setType", String.class);
            setTypeMethod.invoke(h5Info, "Wap");
            A sceneInfo = aClass.getDeclaredConstructor().newInstance();
            Method setPayerClientIpMethod = bClass.getMethod("setPayerClientIp", String.class);
            setPayerClientIpMethod.invoke(sceneInfo, transaction.getUserIp());
            Method setH5InfoMethod = bClass.getMethod("setH5Info", bClass);
            setH5InfoMethod.invoke(sceneInfo, h5Info);
            return sceneInfo;
        } catch (Exception e) {
            throw new PayException(e);
        }
    }

    protected <T> T getPrepayRequest(TransactionEntity transaction, Class<T> clazz, T request) {
        try {
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

    protected <T> TransactionEntity getTransaction(T transaction, Class<T> clazz) {
        String tradeStateStr;
        String successTime;
        String outTradeNo;
        String transactionId;
        try {
            Method getTradeStateMethod = clazz.getMethod("getTradeState");
            Object tradeState = getTradeStateMethod.invoke(transaction);
            Class<?> tradeStateClass = tradeState.getClass();
            Method nameMethod = tradeStateClass.getMethod("name");
            tradeStateStr = (String) nameMethod.invoke(tradeState);
            Method getSuccessTimeMethod = clazz.getMethod("getSuccessTime");
            successTime = (String) getSuccessTimeMethod.invoke(transaction);
            Method getOutTradeNoMethod = clazz.getMethod("getOutTradeNo");
            outTradeNo = (String) getOutTradeNoMethod.invoke(transaction);
            Method getTransactionIdMethod = clazz.getMethod("getTransactionId");
            transactionId = (String) getTransactionIdMethod.invoke(transaction);
        } catch (Exception e) {
            throw new PayException(e);
        }
        String status = TransactionStatus.NOT_PAY;
        LocalDateTime finishTime = null;
        if ("SUCCESS".equals(tradeStateStr)) {
            status = TransactionStatus.SUCCESS;
            finishTime = LocalDateTime.parse(successTime, FORMATTER);
        } else if ("CLOSED".equals(tradeStateStr)) {
            status = TransactionStatus.CLOSED;
        }
        return new TransactionEntity()
                .setTransactionNo(outTradeNo)
                .setStatus(status)
                .setOutTransactionNo(transactionId)
                .setFinishTime(finishTime);
    }

    protected RequestParam getRequestParam(HttpServletRequest request) {
        return new RequestParam.Builder()
                .serialNumber(request.getHeader(Constant.WECHAT_PAY_SERIAL))
                .signature(request.getHeader(Constant.WECHAT_PAY_SIGNATURE))
                .timestamp(request.getHeader(Constant.WECHAT_PAY_TIMESTAMP))
                .nonce(request.getHeader(Constant.WECHAT_PAY_NONCE))
                .body(ServletUtil.getBody(request))
                .build();
    }

    protected <T> TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag, Class<T> clazz) {
        WechatConfigEntity wechatConfig = getWechatConfig(entranceFlag);
        RequestParam requestParam = getRequestParam(request);
        NotificationParser notificationParser = new NotificationParser(WechatPayFactory.getConfig(wechatConfig));
        T transaction;
        try {
            transaction = notificationParser.parse(requestParam, clazz);
        } catch (Exception e) {
            throw new PayException("解析支付通知失败", e);
        }
        return getTransaction(transaction, clazz);
    }

    @Override
    public void refund(TransactionEntity transaction, RefundEntity refund) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        AmountReq amountReq = new AmountReq();
        amountReq.setTotal(transaction.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        amountReq.setRefund(refund.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        amountReq.setCurrency("CNY");
        CreateRequest request = new CreateRequest();
        request.setSubMchid(wechatConfig.getSubMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        request.setOutRefundNo(refund.getRefundNo());
        request.setNotifyUrl(NotifyUrl.getRefundNotifyUrl(transaction.getEntranceFlag()));
        request.setAmount(amountReq);
        RefundService refundService = new RefundService.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        try {
            refundService.create(request);
        } catch (Exception e) {
            throw new PayException("退款失败", e);
        }
    }

    @Override
    public boolean queryRefund(TransactionEntity transaction, RefundEntity refund) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        request.setSubMchid(wechatConfig.getSubMchId());
        request.setOutRefundNo(refund.getRefundNo());
        RefundService refundService = new RefundService.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        Refund refundResult;
        try {
            refundResult = refundService.queryByOutRefundNo(request);
        } catch (Exception e) {
            throw new PayException("查询退款失败", e);
        }
        refund.setOutRefundNo(refundResult.getRefundId());
        if (Status.SUCCESS == refundResult.getStatus()) {
            refund.setStatus(RefundStatus.REFUNDED);
            refund.setFinishTime(LocalDateTime.parse(refundResult.getSuccessTime(), FORMATTER));
            return true;
        } else if (Status.PROCESSING != refundResult.getStatus()) {
            refund.setStatus(RefundStatus.REFUND_FAIL);
            return true;
        }
        return false;
    }

    @Override
    public RefundEntity parseRefundNotify(HttpServletRequest request, String entranceFlag) {
        WechatConfigEntity wechatConfig = getWechatConfig(entranceFlag);
        RequestParam requestParam = getRequestParam(request);
        NotificationParser notificationParser = new NotificationParser(WechatPayFactory.getConfig(wechatConfig));
        RefundNotification refundNotification;
        try {
            refundNotification = notificationParser.parse(requestParam, RefundNotification.class);
        } catch (Exception e) {
            throw new PayException("解析退款通知失败", e);
        }
        String status = RefundStatus.REFUNDING;
        LocalDateTime finishTime = null;
        if (Status.SUCCESS == refundNotification.getRefundStatus()) {
            status = RefundStatus.REFUNDED;
            finishTime = LocalDateTime.parse(refundNotification.getSuccessTime(), FORMATTER);
        } else if (Status.PROCESSING != refundNotification.getRefundStatus()) {
            status = RefundStatus.REFUND_FAIL;
        }
        return new RefundEntity()
                .setRefundNo(refundNotification.getOutRefundNo())
                .setStatus(status)
                .setOutRefundNo(refundNotification.getRefundId())
                .setFinishTime(finishTime);
    }
}
