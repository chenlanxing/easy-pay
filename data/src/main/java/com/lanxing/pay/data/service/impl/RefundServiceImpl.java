package com.lanxing.pay.data.service.impl;

import com.lanxing.pay.data.entity.RefundEntity;
import com.lanxing.pay.data.mapper.RefundMapper;
import com.lanxing.pay.data.service.RefundService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 退款 Service实现类
 *
 * @author chenlanxing
 */
@Service
public class RefundServiceImpl extends ServiceImpl<RefundMapper, RefundEntity> implements RefundService {
}
