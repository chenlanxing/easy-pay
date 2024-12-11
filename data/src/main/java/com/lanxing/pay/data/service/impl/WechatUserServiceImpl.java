package com.lanxing.pay.data.service.impl;

import com.lanxing.pay.data.entity.WechatUserEntity;
import com.lanxing.pay.data.mapper.WechatUserMapper;
import com.lanxing.pay.data.service.WechatUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 微信用户 Service实现类
 *
 * @author chenlanxing
 */
@Service
public class WechatUserServiceImpl extends ServiceImpl<WechatUserMapper, WechatUserEntity> implements WechatUserService {
}
