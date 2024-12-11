package com.lanxing.pay.biz.common.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 异常通知配置参数
 *
 * @author chenlanxing
 */
@Data
@Component
@ConfigurationProperties("exception-notify")
public class ExceptionNotifyProperties {

    private String from;

    private List<String> to;
}
