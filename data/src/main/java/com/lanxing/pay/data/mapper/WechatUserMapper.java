package com.lanxing.pay.data.mapper;

import com.lanxing.pay.data.entity.WechatUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信用户 Mapper
 *
 * @author chenlanxing
 */
@Mapper
public interface WechatUserMapper extends BaseMapper<WechatUserEntity> {
}