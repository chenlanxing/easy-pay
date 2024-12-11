package com.lanxing.pay.biz.common.config;

import com.lanxing.pay.biz.common.model.MyExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author chenlanxing
 */
@Configuration
public class ExecutorConfig {

    @Bean("exceptionNotifyExecutor")
    public Executor exceptionNotifyExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        MyExecutor executor = new MyExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("exception-notify-exec-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("callbackExecutor")
    public Executor callbackExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        MyExecutor executor = new MyExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("callback-exec-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("callbackNotifyExecutor")
    public Executor callbackNotifyExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        MyExecutor executor = new MyExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("callback-notify-exec-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
