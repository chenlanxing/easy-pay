package com.lanxing.pay.core.wechat;

import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.service.WechatConfigService;
import com.wechat.pay.java.service.partnerpayments.h5.H5Service;
import com.wechat.pay.java.service.partnerpayments.h5.model.Amount;
import com.wechat.pay.java.service.partnerpayments.h5.model.CloseOrderRequest;
import com.wechat.pay.java.service.partnerpayments.h5.model.H5Info;
import com.wechat.pay.java.service.partnerpayments.h5.model.PrepayRequest;
import com.wechat.pay.java.service.partnerpayments.h5.model.PrepayResponse;
import com.wechat.pay.java.service.partnerpayments.h5.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.partnerpayments.h5.model.SceneInfo;
import com.wechat.pay.java.service.partnerpayments.h5.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务商H5支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("wechatPartnerH5")
public class PartnerH5PayService extends PartnerWechatPayService {

    @Autowired
    public void setWechatConfigService(WechatConfigService wechatConfigService) {
        this.wechatConfigService = wechatConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        PrepayRequest request = getPrepayRequest(transaction, wechatConfig, PrepayRequest.class);
        request.setAmount(getAmount(transaction, Amount.class));
        request.setSceneInfo(getSceneInfo(transaction, SceneInfo.class, H5Info.class));
        H5Service h5Service = new H5Service.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        PrepayResponse response;
        try {
            response = h5Service.prepay(request);
        } catch (Exception e) {
            throw new PayException("预支付失败", e);
        }
        return response.getH5Url();
    }

    @Override
    public void closePay(TransactionEntity transaction) {
        WechatConfigEntity wechatConfig = getWechatConfig(transaction.getEntranceFlag());
        CloseOrderRequest request = new CloseOrderRequest();
        request.setSpMchid(wechatConfig.getMchId());
        request.setSubMchid(wechatConfig.getSubMchId());
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
        request.setSpMchid(wechatConfig.getMchId());
        request.setSubMchid(wechatConfig.getSubMchId());
        request.setOutTradeNo(transaction.getTransactionNo());
        H5Service h5Service = new H5Service.Builder().config(WechatPayFactory.getConfig(wechatConfig)).build();
        Transaction transactionResult;
        try {
            transactionResult = h5Service.queryOrderByOutTradeNo(request);
        } catch (Exception e) {
            throw new PayException("查询支付失败", e);
        }
        return updateTransaction(transaction, transactionResult, Transaction.class);
    }

    @Override
    public TransactionEntity parsePayNotify(HttpServletRequest request, String entranceFlag) {
        return parsePayNotify(request, entranceFlag, Transaction.class);
    }
}
