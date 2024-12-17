package com.lanxing.pay.data.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class RefundEntity {

    public static final String ID = "id";
    public static final String TRANSACTION_NO = "transaction_no";
    public static final String REFUND_NO = "refund_no";
    public static final String AMOUNT = "amount";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String FINISH_TIME = "finish_time";
    public static final String OUT_REFUND_NO = "out_refund_no";
    public static final String BIZ_FLAG = "biz_flag";
    public static final String BIZ_DATA_NO = "biz_data_no";
    public static final String BIZ_ATTACH = "biz_attach";
    public static final String BIZ_CALLBACK_URL = "biz_callback_url";
    public static final String BIZ_CALLBACK_STATUS = "biz_callback_status";
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 交易编号
     */
    private String transactionNo;
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 完成时间
     */
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
    /**
     * 业务回调地址
     */
    private String bizCallbackUrl;
    /**
     * 额外参数
     */
    private String extraParam;
}
