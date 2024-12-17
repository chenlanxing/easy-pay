package com.lanxing.pay.core.alipay;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alipay.v3.api.AlipayTradeApi;
import com.alipay.v3.api.AlipayTradeFastpayRefundApi;
import com.alipay.v3.model.AlipayTradeCloseModel;
import com.alipay.v3.model.AlipayTradeFastpayRefundQueryModel;
import com.alipay.v3.model.AlipayTradeFastpayRefundQueryResponseModel;
import com.alipay.v3.model.AlipayTradeQueryModel;
import com.alipay.v3.model.AlipayTradeQueryResponseModel;
import com.alipay.v3.model.AlipayTradeRefundModel;
import com.alipay.v3.util.model.CustomizedParams;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lanxing.pay.core.NotifyUrl;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.core.PayService;
import com.lanxing.pay.data.constant.RefundStatus;
import com.lanxing.pay.data.constant.TransactionStatus;
import com.lanxing.pay.data.entity.AlipayConfigEntity;
import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.service.AlipayConfigService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 支付宝支付基类
 *
 * @author chenlanxing
 */
@Slf4j
public abstract class AlipayPayService implements PayService {

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);

    protected AlipayConfigService alipayConfigService;

    protected AlipayConfigEntity getAlipayConfig(String entranceFlag) {
        AlipayConfigEntity alipayConfig = alipayConfigService.getOne(Wrappers.<AlipayConfigEntity>lambdaQuery()
                .eq(AlipayConfigEntity::getEntranceFlag, entranceFlag));
        Assert.notNull(alipayConfig, () -> new PayException("支付宝配置不存在"));
        return alipayConfig;
    }

    protected Map<String, Object> getBizParams(TransactionEntity transaction, Map<String, Object> bizContent) {
        bizContent.put("out_trade_no", transaction.getTransactionNo());
        bizContent.put("total_amount", transaction.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        bizContent.put("subject", transaction.getDescription());
        bizContent.put("time_expire", transaction.getExpireTime().format(FORMATTER));
        bizContent.put("notify_url", NotifyUrl.getPayNotifyUrl(transaction.getEntranceFlag()));
        Map<String, Object> bizParams = new HashMap<>();
        bizParams.put("biz_content", bizContent);
        return bizParams;
    }

    @Override
    public void closePay(TransactionEntity transaction) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(transaction.getEntranceFlag());
        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(transaction.getTransactionNo());
        try {
            new AlipayTradeApi(AlipayPayFactory.getClient(alipayConfig)).close(model);
        } catch (Exception e) {
            throw new PayException("关闭支付失败", e);
        }
    }

    @Override
    public boolean queryPay(TransactionEntity transaction) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(transaction.getEntranceFlag());
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(transaction.getTransactionNo());
        CustomizedParams params = null;
        if (StrUtil.isNotEmpty(alipayConfig.getAuthToken())) {
            params = new CustomizedParams();
            params.setAppAuthToken(alipayConfig.getAuthToken());
        }
        AlipayTradeQueryResponseModel response;
        try {
            response = new AlipayTradeApi(AlipayPayFactory.getClient(alipayConfig)).query(model, params);
        } catch (Exception e) {
            throw new PayException("查询支付失败", e);
        }

        transaction.setOutTransactionNo(response.getTradeNo());
        if ("TRADE_SUCCESS".equals(response.getTradeStatus())) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setFinishTime(LocalDateTime.parse(Objects.requireNonNull(response.getSendPayDate()), FORMATTER));
            return true;
        } else if ("TRADE_CLOSED".equals(response.getTradeStatus())) {
            transaction.setStatus(TransactionStatus.CLOSED);
            return true;
        }
        return false;
    }

    @Override
    public TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(entranceFlag);
        return null;
    }

    @Override
    public void refund(TransactionEntity transaction, RefundEntity refund) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(transaction.getEntranceFlag());
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(transaction.getTransactionNo());
        model.setOutRequestNo(refund.getRefundNo());
        model.setRefundAmount(refund.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        model.setRefundReason(refund.getDescription());
        CustomizedParams params = null;
        if (StrUtil.isNotEmpty(alipayConfig.getAuthToken())) {
            params = new CustomizedParams();
            params.setAppAuthToken(alipayConfig.getAuthToken());
        }
        try {
            new AlipayTradeApi(AlipayPayFactory.getClient(alipayConfig)).refund(model, params);
        } catch (Exception e) {
            throw new PayException("退款失败", e);
        }
    }

    @Override
    public boolean queryRefund(TransactionEntity transaction, RefundEntity refund) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(transaction.getEntranceFlag());
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(transaction.getTransactionNo());
        model.setOutRequestNo(refund.getRefundNo());
        model.setQueryOptions(List.of("gmt_refund_pay"));
        CustomizedParams params = null;
        if (StrUtil.isNotEmpty(alipayConfig.getAuthToken())) {
            params = new CustomizedParams();
            params.setAppAuthToken(alipayConfig.getAuthToken());
        }
        AlipayTradeFastpayRefundQueryResponseModel response;
        try {
            response = new AlipayTradeFastpayRefundApi(AlipayPayFactory.getClient(alipayConfig)).query(model, params);
        } catch (Exception e) {
            throw new PayException("查询退款失败", e);
        }
        refund.setOutRefundNo(response.getOutRequestNo());
        if ("REFUND_SUCCESS".equals(response.getRefundStatus())) {
            refund.setStatus(RefundStatus.REFUNDED);
            refund.setFinishTime(LocalDateTime.parse(Objects.requireNonNull(response.getGmtRefundPay()), FORMATTER));
            return true;
        } else if (LocalDateTime.now().isAfter(refund.getCreateTime().plusSeconds(10))) {
            refund.setStatus(RefundStatus.REFUND_FAIL);
            return true;
        }
        return false;
    }

    @Override
    public RefundEntity parseRefundNotify(HttpServletRequest request, String entranceFlag) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(entranceFlag);
        return null;
    }
}
