package com.lanxing.pay.biz.common.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.lanxing.pay.biz.common.constant.MdcConst;
import com.lanxing.pay.biz.model.APIResult;
import com.lanxing.pay.core.PayException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author chenlanxing
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ExceptionNotifyProperties exceptionNotifyProperties;

    @Autowired
    @Qualifier("exceptionNotifyExecutor")
    private Executor exceptionNotifyExecutor;

    @ExceptionHandler(BindException.class)
    public APIResult handleBindException(BindException e) {
        String msg = e.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
        log.warn("参数校验异常：{}", msg);
        return APIResult.fail(msg);
    }

    @ExceptionHandler(BizException.class)
    public APIResult handleBizException(BizException e) {
        String msg = e.getMessage();
        log.warn("业务异常：{}", msg);
        return APIResult.fail(msg);
    }

    @ExceptionHandler(PayException.class)
    public APIResult handlePayException(PayException e) {
        String msg = e.getMessage();
        log.warn("支付异常：{}", msg);
        return APIResult.fail(msg);
    }

    @ExceptionHandler(Exception.class)
    public APIResult handleException(Exception e) {
        CompletableFuture.runAsync(() -> {
            String content = "追踪ID：" + MDC.get(MdcConst.TRACE_ID) + "\n" +
                    ExceptionUtil.stacktraceToString(e);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(exceptionNotifyProperties.getFrom());
            message.setTo(exceptionNotifyProperties.getTo().toArray(new String[0]));
            message.setSubject("支付系统错误：" + e.getMessage());
            message.setText(content);
            mailSender.send(message);
        }, exceptionNotifyExecutor);
        log.error("发生错误：{}", e.getMessage(), e);
        return APIResult.error();
    }
}
