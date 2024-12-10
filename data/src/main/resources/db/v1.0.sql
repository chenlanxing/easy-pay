DROP TABLE IF EXISTS `entrance`;
CREATE TABLE `entrance`
(
    `id`                    BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `entrance_flag`         VARCHAR(32)   NOT NULL COMMENT '入口标识',
    `impl_bean_name`        VARCHAR(32)   NOT NULL COMMENT '实现Bean名称',
    `default_callback_data` VARCHAR(1000) NOT NULL COMMENT '默认回调返回数据',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`entrance_flag`)
) COMMENT = '入口';

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction`
(
    `id`                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `entrance_flag`       VARCHAR(32)     NOT NULL COMMENT '入口标识',
    `transaction_no`      VARCHAR(32)     NOT NULL COMMENT '交易编号',
    `amount`              DECIMAL(10, 10) NOT NULL COMMENT '金额',
    `description`         VARCHAR(100)    NOT NULL COMMENT '描述',
    `status`              VARCHAR(16)     NOT NULL COMMENT 'not-pay, success, closed',
    `create_time`         DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_time`         DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `expire_time`         DATETIME(3)     NOT NULL COMMENT '过期时间',
    `finish_time`         DATETIME(3)     NULL COMMENT '完成时间',
    `out_transaction_no`  VARCHAR(64)     NULL COMMENT '外部交易编号',
    `user_id`             VARCHAR(32)     NULL COMMENT '用户ID',
    `user_ip`             VARCHAR(32)     NOT NULL COMMENT '用户IP',
    `biz_flag`            VARCHAR(32)     NOT NULL COMMENT '业务标识',
    `biz_data_no`         VARCHAR(32)     NOT NULL COMMENT '业务数据编号',
    `biz_attach`          VARCHAR(1000)   NULL COMMENT '业务附加信息',
    `biz_callback_url`    VARCHAR(256)    NULL COMMENT '业务回调地址',
    `biz_callback_status` VARCHAR(16)     NULL COMMENT 'not-execute, success, fail',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`transaction_no`),
    KEY (`entrance_flag`),
    KEY (`biz_data_no`, `biz_flag`)
) COMMENT = '交易';

DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund`
(
    `id`                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `transaction_no`      VARCHAR(32)     NOT NULL COMMENT '交易编号',
    `refund_no`           VARCHAR(32)     NOT NULL COMMENT '退款编号',
    `amount`              DECIMAL(10, 10) NOT NULL COMMENT '金额',
    `description`         VARCHAR(100)    NOT NULL COMMENT '描述',
    `status`              VARCHAR(16)     NOT NULL COMMENT 'refunding, refunded, refund-fail',
    `create_time`         DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_time`         DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `finish_time`         DATETIME(3)     NULL COMMENT '完成时间',
    `out_refund_no`       VARCHAR(32)     NULL COMMENT '外部退款编号',
    `biz_flag`            VARCHAR(32)     NOT NULL COMMENT '业务标识',
    `biz_data_no`         VARCHAR(32)     NOT NULL COMMENT '业务数据编号',
    `biz_attach`          VARCHAR(1000)   NULL COMMENT '业务附加信息',
    `biz_callback_url`    VARCHAR(256)    NULL COMMENT '业务回调地址',
    `biz_callback_status` VARCHAR(16)     NULL COMMENT 'not-execute, success, fail',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`refund_no`),
    KEY (`transaction_no`),
    KEY (`biz_data_no`, `biz_flag`)
) COMMENT = '退款';