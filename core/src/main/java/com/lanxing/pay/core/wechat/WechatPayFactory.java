package com.lanxing.pay.core.wechat;

import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信支付工厂
 *
 * @author chenlanxing
 */
public class WechatPayFactory {

    private static final Map<String, RSAAutoCertificateConfig> CONFIG_MAP = new ConcurrentHashMap<>();

    public static synchronized RSAAutoCertificateConfig getConfig(WechatConfigEntity wechatConfig) {
        if (CONFIG_MAP.containsKey(wechatConfig.getMchId())) {
            return CONFIG_MAP.get(wechatConfig.getMchId());
        }
        RSAAutoCertificateConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(wechatConfig.getMchId())
                .merchantSerialNumber(wechatConfig.getMchSerialNo())
                .apiV3Key(wechatConfig.getApiV3Key())
                .privateKey(wechatConfig.getPrivateKey())
                .build();
        CONFIG_MAP.put(wechatConfig.getMchId(), config);
        return config;
    }
}
