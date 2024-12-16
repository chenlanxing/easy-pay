package com.lanxing.pay.core.alipay;

import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.Configuration;
import com.alipay.v3.util.model.AlipayConfig;
import com.lanxing.pay.data.entity.AlipayConfigEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付宝支付工厂
 *
 * @author chenlanxing
 */
public class AlipayPayFactory {

    private static final Map<String, ApiClient> CLIENTS = new ConcurrentHashMap<>();

    public static ApiClient getClient(AlipayConfigEntity alipayConfig) throws ApiException {
        if (CLIENTS.containsKey(alipayConfig.getAppId())) {
            return CLIENTS.get(alipayConfig.getAppId());
        }
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl("https://openapi.alipay.com");
        config.setAppId(alipayConfig.getAppId());
        config.setPrivateKey(alipayConfig.getPrivateKey());
        config.setAlipayPublicKey(alipayConfig.getPublicKey());
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setAlipayConfig(config);
        CLIENTS.put(alipayConfig.getAppId(), defaultClient);
        return defaultClient;
    }
}
