package com.lanxing.pay.biz.config;

import cn.hutool.core.util.RandomUtil;
import com.lanxing.pay.biz.constant.MdcConst;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WEB配置
 *
 * @author chenlanxing
 */
@SuppressWarnings("all")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                MDC.put(MdcConst.TRACE_ID, RandomUtil.randomString(32).toLowerCase());
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                MDC.remove(MdcConst.TRACE_ID);
            }
        }).addPathPatterns("/**");
    }
}
