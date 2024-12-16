package com.lanxing.pay.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 支付宝配置
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class AlipayConfigEntity {

    public static final String ID = "id";
    public static final String ENTRANCE_FLAG = "entrance_flag";
    public static final String APP_ID = "app_id";
    public static final String PRIVATE_KEY = "private_key";
    public static final String PUBLIC_KEY = "public_key";
    public static final String AUTH_TOKEN = "auth_token";
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
     * 应用ID
     */
    private String appId;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 代调用授权Token
     */
    private String authToken;
}
