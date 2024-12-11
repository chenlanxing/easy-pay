package com.lanxing.pay.core;

import cn.hutool.core.util.StrUtil;

/**
 * 通知地址
 *
 * @author chenlanxing
 */
public class NotifyUrl {

    private static final String DOMAIN = System.getenv("domain");

    public static String getPayNotifyUrl(String entranceFlag) {
        return StrUtil.format("{}/callback/payNotify/{}", DOMAIN, entranceFlag);
    }

    public static String getRefundNotifyUrl(String entranceFlag) {
        return StrUtil.format("{}/callback/refundNotify/{}", DOMAIN, entranceFlag);
    }
}
