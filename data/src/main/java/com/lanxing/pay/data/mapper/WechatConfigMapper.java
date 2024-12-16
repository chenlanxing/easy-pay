package com.lanxing.pay.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lanxing.pay.data.entity.WechatConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信配置 Mapper
 *
 * @author chenlanxing
 */
@Mapper
public interface WechatConfigMapper extends BaseMapper<WechatConfigEntity> {
}
