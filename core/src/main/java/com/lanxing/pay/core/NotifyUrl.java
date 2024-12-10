package com.lanxing.pay.core;

import cn.hutool.core.util.StrUtil;

/**
 * 通知地址
 *
 * @author chenlanxing
 */
public class NotifyUrl {

    public static String getPayNotifyUrl(String domain, String entranceFlag) {
        return StrUtil.format("{}/callback/payNotify/{}", domain, entranceFlag);
    }

    public static String getRefundNotifyUrl(String domain, String entranceFlag) {
        return StrUtil.format("{}/callback/refundNotify/{}", domain, entranceFlag);
    }
}
