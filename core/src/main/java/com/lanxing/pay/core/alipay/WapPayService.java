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
 * 手机网站支付
 *
 * @author chenlanxing
 */
@Slf4j
@Service("alipayWap")
public class WapPayService extends AlipayPayService {

    @Autowired
    public void setAlipayConfigService(AlipayConfigService alipayConfigService) {
        this.alipayConfigService = alipayConfigService;
    }

    @Override
    public Object prepay(TransactionEntity transaction) {
        AlipayConfigEntity alipayConfig = getAlipayConfig(transaction.getEntranceFlag());
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("product_code", "QUICK_WAP_WAY");
        bizContent.put("auth_token", JSON.parseObject(transaction.getExtraParam()).getString("authToken"));
        bizContent.put("quit_url", JSON.parseObject(transaction.getExtraParam()).getString("quitUrl"));
        Map<String, Object> bizParams = getBizParams(transaction, bizContent);
        try {
            return new GenericExecuteApi(AlipayPayFactory.getClient(alipayConfig))
                    .pageExecute("alipay.trade.wap.pay", "GET", bizParams, null, alipayConfig.getAuthToken(), null);
        } catch (Exception e) {
            throw new PayException("预支付失败", e);
        }
    }
}
