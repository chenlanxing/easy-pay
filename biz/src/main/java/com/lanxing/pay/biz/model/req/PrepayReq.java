package com.lanxing.pay.biz.model.req;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 预支付请求
 *
 * @author chenlanxing
 */
@Data
public class PrepayReq {

    /**
     * 入口标识
     */
    @NotBlank(message = "入口标识不能为空")
    private String entranceFlag;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额最小为0.01")
    private BigDecimal amount;

    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空")
    private String description;

    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空")
    private LocalDateTime expireTime;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户IP
     */
    private String userIp;

    /**
     * 业务标识
     */
    @NotBlank(message = "业务标识不能为空")
    private String bizFlag;

    /**
     * 业务数据编号
     */
    @NotBlank(message = "业务数据编号不能为空")
    private String bizDataNo;

    /**
     * 业务附加信息
     */
    private String bizAttach;

    /**
     * 业务回调地址
     */
    private String bizCallbackUrl;

    /**
     * 额外参数
     */
    private Map<String, Object> extraParams;
}
