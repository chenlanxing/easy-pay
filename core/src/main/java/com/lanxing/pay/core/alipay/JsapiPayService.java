package com.lanxing.pay.core.alipay;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.v3.api.AlipayTradeApi;
import com.alipay.v3.model.AlipayTradeCreateModel;
import com.alipay.v3.model.AlipayTradeCreateResponseModel;
import com.alipay.v3.util.model.CustomizedParams;
import com.lanxing.pay.core.NotifyUrl;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.entity.AlipayConfigEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.service.AlipayConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

/**
 * JSAPI支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("alipayJsapi")
public class JsapiPayService extends AlipayPayService {

    @Autowired
    public void setAlipayConfigService(AlipayConfigService alipayConfigService) {
        this.alipayConfigService = alipayConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(transaction.getEntranceFlag());
        AlipayTradeCreateModel model = new AlipayTradeCreateModel();
        model.setProductCode("JSAPI_PAY");
        model.setOutTradeNo(transaction.getTransactionNo());
        model.setTotalAmount(transaction.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        model.setSubject(transaction.getDescription());
        model.setTimeExpire(transaction.getExpireTime().format(FORMATTER));
        model.setNotifyUrl(NotifyUrl.getPayNotifyUrl(transaction.getEntranceFlag()));
        model.setOpAppId(alipayConfig.getAppId());
        model.setBuyerOpenId(JSON.parseObject(transaction.getExtraParam()).getString("openId"));
        CustomizedParams params = null;
        if (StrUtil.isNotEmpty(alipayConfig.getAuthToken())) {
            params = new CustomizedParams();
            params.setAppAuthToken(alipayConfig.getAuthToken());
        }
        AlipayTradeCreateResponseModel response;
        try {
            response = new AlipayTradeApi(AlipayPayFactory.getClient(alipayConfig)).create(model, params);
        } catch (Exception e) {
            throw new PayException("预支付失败", e);
        }
        return response.getTradeNo();
    }
}
