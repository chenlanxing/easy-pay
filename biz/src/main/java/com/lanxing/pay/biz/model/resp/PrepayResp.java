package com.lanxing.pay.biz.model.resp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 预支付响应
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class PrepayResp {

    /**
     * 交易编号
     */
    private String transactionNo;

    /**
     * 预支付信息
     */
    private Object prepayInfo;
}
