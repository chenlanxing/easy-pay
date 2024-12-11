package com.lanxing.pay.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信配置
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class WechatConfigEntity {

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
     * 商户ID
     */
    private String mchId;

    /**
     * 子应用ID
     */
    private String subAppId;

    /**
     * 子商户ID
     */
    private String subMchId;

    /**
     * 商户证书序号
     */
    private String mchSerialNo;

    /**
     * API V3密钥
     */
    private String apiV3Key;

    /**
     * 私钥
     */
    private String privateKey;

    public static final String ID = "id";

    public static final String ENTRANCE_FLAG = "entrance_flag";

    public static final String APP_ID = "app_id";

    public static final String MCH_ID = "mch_id";

    public static final String SUB_APP_ID = "sub_app_id";

    public static final String SUB_MCH_ID = "sub_mch_id";

    public static final String MCH_SERIAL_NO = "mch_serial_no";

    public static final String API_V3_KEY = "api_v3_key";

    public static final String PRIVATE_KEY = "private_key";
}
