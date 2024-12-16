package com.lanxing.pay.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lanxing.pay.data.entity.TransactionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易 Mapper
 *
 * @author chenlanxing
 */
@Mapper
public interface TransactionMapper extends BaseMapper<TransactionEntity> {
}
