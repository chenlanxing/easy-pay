package com.lanxing.pay.core;

/**
 * 支付异常
 *
 * @author chenlanxing
 */
public class PayException extends RuntimeException {

    private static final long serialVersionUID = 7292811118501255105L;

    public PayException(String message) {
        super(message);
    }

    public PayException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayException(Throwable cause) {
        super(cause);
    }
}
