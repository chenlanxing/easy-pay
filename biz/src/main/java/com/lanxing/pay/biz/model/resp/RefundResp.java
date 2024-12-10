package com.lanxing.pay.biz.model.resp;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款响应
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class RefundResp {

    /**
     * 退款编号
     */
    private String refundNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 描述
     */
    private String description;

    /**
     * refunding, refunded, refund-fail
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
     * 外部退款编号
     */
    private String outRefundNo;

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
}
