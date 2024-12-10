package com.lanxing.pay.biz.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 回调接口
 *
 * @author chenlanxing
 */
public interface CallbackService {

    /**
     * 支付通知
     *
     * @param entranceFlag .
     * @param request      .
     * @return .
     */
    String payNotify(String entranceFlag, HttpServletRequest request);

    /**
     * 退款通知
     *
     * @param entranceFlag .
     * @param request      .
     * @return .
     */
    String refundNotify(String entranceFlag, HttpServletRequest request);
}
