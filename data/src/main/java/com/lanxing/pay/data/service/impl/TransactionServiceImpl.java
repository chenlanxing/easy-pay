package com.lanxing.pay.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanxing.pay.data.entity.TransactionEntity;
import com.lanxing.pay.data.mapper.TransactionMapper;
import com.lanxing.pay.data.service.TransactionService;
import org.springframework.stereotype.Service;

/**
 * 交易 Service实现类
 *
 * @author chenlanxing
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, TransactionEntity> implements TransactionService {
}
