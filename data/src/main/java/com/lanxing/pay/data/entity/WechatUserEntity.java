package com.lanxing.pay.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信用户
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class WechatUserEntity {

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String APP_ID = "app_id";
    public static final String OPEN_ID = "open_id";
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 应用ID
     */
    private String appId;
    /**
     * OpenID
     */
    private String openId;
}
