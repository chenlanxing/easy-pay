package com.lanxing.pay.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lanxing.pay.data.entity.AlipayConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付宝配置 Mapper
 *
 * @author chenlanxing
 */
@Mapper
public interface AlipayConfigMapper extends BaseMapper<AlipayConfigEntity> {
}
