package com.lanxing.pay.biz.constant;

/**
 * Redis锁常量
 *
 * @author chenlanxing
 */
public class RedisLockConst {

    public static final String PREPAY_LOCK = "lock:prepay:{}:{}";

    public static final String TRANSACTION_LOCK = "lock:transaction:{}";

    public static final String REFUND_REQUEST_LOCK = "lock:refund:request:{}";

    public static final String REFUND_LOCK = "lock:refund:{}";
}
