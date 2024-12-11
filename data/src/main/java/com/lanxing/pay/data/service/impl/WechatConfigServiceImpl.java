package com.lanxing.pay.data.service.impl;

import com.lanxing.pay.data.entity.WechatConfigEntity;
import com.lanxing.pay.data.mapper.WechatConfigMapper;
import com.lanxing.pay.data.service.WechatConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 微信配置 Service实现类
 *
 * @author chenlanxing
 */
@Service
public class WechatConfigServiceImpl extends ServiceImpl<WechatConfigMapper, WechatConfigEntity> implements WechatConfigService {
}
