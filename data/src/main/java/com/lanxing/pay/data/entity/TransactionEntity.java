package com.lanxing.pay.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author chenhaizhuang
 */
@Data
@Accessors(chain = true)
public class TransactionEntity {

    /**
     * 入口标识
     */
    private String entranceFlag;
}
