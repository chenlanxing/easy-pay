package com.lanxing.pay.biz.exception;

/**
 * 业务异常
 *
 * @author chenlanxing
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -3126850320755869672L;

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }
}