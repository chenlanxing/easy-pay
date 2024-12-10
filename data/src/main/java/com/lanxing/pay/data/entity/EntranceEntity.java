package com.lanxing.pay.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 入口
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class EntranceEntity {

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
     * 实现Bean名称
     */
    private String implBeanName;

    /**
     * 默认回调返回数据
     */
    private String defaultCallbackData;

    public static final String ID = "id";

    public static final String ENTRANCE_FLAG = "entrance_flag";

    public static final String IMPL_BEAN_NAME = "impl_bean_name";

    public static final String DEFAULT_CALLBACK_DATA = "default_callback_data";
}
