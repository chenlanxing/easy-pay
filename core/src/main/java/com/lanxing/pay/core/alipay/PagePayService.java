package com.lanxing.pay.core.alipay;

import com.alibaba.fastjson.JSON;
import com.alipay.v3.util.GenericExecuteApi;
import com.lanxing.pay.core.PayException;
import com.lanxing.pay.data.entity.AlipayConfigEntity;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.service.AlipayConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 电脑网站支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("alipayPage")
public class PagePayService extends AlipayPayService {

    @Autowired
    public void setAlipayConfigService(AlipayConfigService alipayConfigService) {
        this.alipayConfigService = alipayConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(transaction.getEntranceFlag());
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("qr_pay_mode", JSON.parseObject(transaction.getExtraParam()).getString("qrPayMode"));
        bizContent.put("qrcode_width", JSON.parseObject(transaction.getExtraParam()).getString("qrcodeWidth"));
        Map<String, Object> subMerchant = new HashMap<>();
        subMerchant.put("merchant_id", alipayConfig.getAuthToken());
        subMerchant.put("merchant_type", "alipay");
        bizContent.put("sub_merchant", subMerchant);
        Map<String, Object> bizParams = getBizParams(transaction, bizContent);
        try {
            return new GenericExecuteApi(AlipayPayFactory.getClient(alipayConfig))
                    .pageExecute("alipay.trade.page.pay", "GET", bizParams, null, alipayConfig.getAuthToken(), null);
        } catch (Exception e) {
            throw new PayException("预支付失败", e);
        }
    }
}
