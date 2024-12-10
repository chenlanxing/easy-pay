package com.lanxing.pay.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 默认支付实现类
 *
 * @author chenhaizhuang
 */
@Slf4j
@Service
@Primary
public class DefaultPayService implements PayService {

    @Autowired
    private ApplicationContext applicationContext;

    private PayService select() {
        return null;
    }
}
