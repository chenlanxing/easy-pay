package com.lanxing.pay.biz.model.resp;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易响应
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class TransactionResp {

    /**
     * 交易编号
     */
    private String transactionNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 描述
     */
    private String description;

    /**
     * not-pay, success, closed
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_MS_PATTERN)
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MS_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_MS_PATTERN)
    private LocalDateTime finishTime;

    /**
     * 外部交易编号
     */
    private String outTransactionNo;

    /**
     * 业务标识
     */
    private String bizFlag;

    /**
     * 业务数据编号
     */
    private String bizDataNo;

    /**
     * 业务附加信息
     */
    private String bizAttach;

    /**
     * 业务回调地址
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String bizCallbackUrl;
}
