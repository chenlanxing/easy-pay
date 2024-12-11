package com.lanxing.pay.biz.model;

import cn.hutool.core.util.RandomUtil;
import com.lanxing.pay.biz.common.constant.MdcConst;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 自定义线程池
 *
 * @author chenlanxing
 */
@SuppressWarnings("all")
public class MyExecutor extends ThreadPoolTaskExecutor {

    private static final long serialVersionUID = -5555150870243323199L;

    public MyExecutor() {
        super();
    }

    @Override
    public void execute(Runnable task) {
        super.execute(wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task, MDC.getCopyOfContextMap()));
    }

    private <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    private Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

    private void setTraceIdIfAbsent() {
        if (MDC.get(MdcConst.TRACE_ID) == null) {
            MDC.put(MdcConst.TRACE_ID, RandomUtil.randomString(32).toLowerCase());
        }
    }
}
