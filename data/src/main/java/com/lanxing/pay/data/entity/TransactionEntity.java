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
 * 交易
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class TransactionEntity {

    public static final String ID = "id";
    public static final String ENTRANCE_FLAG = "entrance_flag";
    public static final String TRANSACTION_NO = "transaction_no";
    public static final String AMOUNT = "amount";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String EXPIRE_TIME = "expire_time";
    public static final String FINISH_TIME = "finish_time";
    public static final String OUT_TRANSACTION_NO = "out_transaction_no";
    public static final String USER_ID = "user_id";
    public static final String USER_IP = "user_ip";
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
     * 入口标识
     */
    private String entranceFlag;
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 外部交易编号
     */
    private String outTransactionNo;
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
