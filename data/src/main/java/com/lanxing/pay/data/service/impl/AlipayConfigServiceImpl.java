package com.lanxing.pay.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanxing.pay.data.entity.AlipayConfigEntity;
import com.lanxing.pay.data.mapper.AlipayConfigMapper;
import com.lanxing.pay.data.service.AlipayConfigService;
import org.springframework.stereotype.Service;

/**
 * 支付宝配置 Service实现类
 *
 * @author chenlanxing
 */
@Service
public class AlipayConfigServiceImpl extends ServiceImpl<AlipayConfigMapper, AlipayConfigEntity> implements AlipayConfigService {
}
