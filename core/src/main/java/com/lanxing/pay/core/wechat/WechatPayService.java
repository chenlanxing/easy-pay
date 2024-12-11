package com.lanxing.pay.core.wechat;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lanxing.pay.core.NotifyUrl;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.core.PayService;
import com.lanxing.pay.data.constant.RefundStatus;
import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.service.WechatConfigService;
import com.wechat.pay.java.core.http.Constant;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.RefundNotification;
import com.wechat.pay.java.service.refund.model.Status;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 微信支付基类
 *
 * @author chenlanxing
 */
public abstract class WechatPayService implements PayService {

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DatePattern.UTC_WITH_XXX_OFFSET_PATTERN);

    protected WechatConfigService wechatConfigService;

    protected WechatConfigEntity getWechatConfig(String entranceFlag) {
        WechatConfigEntity wechatConfig = wechatConfigService.getOne(Wrappers.<WechatConfigEntity>lambdaQuery()
                .eq(WechatConfigEntity::getEntranceFlag, entranceFlag));
        Assert.notNull(wechatConfig, () -> new PayException("微信配置不存在"));
        return wechatConfig;
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

        try {
            RefundService refundService = new RefundService.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
            refundService.create(request);
        } catch (Exception e) {
            throw new PayException("退款失败", e);
        }
    }

    @Override
    public boolean queryRefund(TransactionEntity transaction, RefundEntity refund) {
        return false;
    }

    @Override
    public RefundEntity parseRefundNotify(HttpServletRequest request, String entranceFlag) {
        WechatConfigEntity wechatConfig = getWechatConfig(entranceFlag);

        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(request.getHeader(Constant.WECHAT_PAY_SERIAL))
                .signature(request.getHeader(Constant.WECHAT_PAY_SIGNATURE))
                .timestamp(request.getHeader(Constant.WECHAT_PAY_TIMESTAMP))
                .nonce(request.getHeader(Constant.WECHAT_PAY_NONCE))
                .body(ServletUtil.getBody(request))
                .build();

        RefundNotification refundNotification;
        try {
            NotificationParser notificationParser = new NotificationParser(WechatPayFactory.getConfig(wechatConfig));
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
