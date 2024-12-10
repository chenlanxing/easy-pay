package com.lanxing.pay.biz.controller;

import com.lanxing.pay.biz.service.CallbackService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 回调接口
 *
 * @author chenlanxing
 */
@RestController
@RequestMapping("/callback")
public class CallbackController {

    @Autowired
    private CallbackService callbackService;

    /**
     * 支付通知
     *
     * @param entranceFlag .
     * @param request      .
     * @param response     .
     */
    @RequestMapping("/payNotify/{entranceFlag}")
    @SneakyThrows
    public void payNotify(@PathVariable String entranceFlag, HttpServletRequest request, HttpServletResponse response) {
        String result = callbackService.payNotify(entranceFlag, request);
        response.getWriter().write(result);
    }

    /**
     * 退款通知
     *
     * @param entranceFlag .
     * @param request      .
     * @param response     .
     */
    @RequestMapping("/refundNotify/{entranceFlag}")
    @SneakyThrows
    public void refundNotify(@PathVariable String entranceFlag, HttpServletRequest request, HttpServletResponse response) {
        String result = callbackService.refundNotify(entranceFlag, request);
        response.getWriter().write(result);
    }
}
