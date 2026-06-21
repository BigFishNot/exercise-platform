-- ============================================================
-- exercise 数据库增量脚本 v5 - 身体数据
-- 设计:
--   - 每天每个用户最多一条记录 (uk_user_date)
--   - 当天重复提交 -> update 而非 insert
--   - bmi 由后端按 weight(kg) / height(m)^2 计算并冗余存储
--   - height 不存这里, 取 user_info.height (用于 BMI 计算)
-- ============================================================

USE `exercise`;

DROP TABLE IF EXISTS `body_data`;
CREATE TABLE `body_data` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     VARCHAR(32)  NOT NULL COMMENT '所属用户ID',
    `record_date` DATE         NOT NULL COMMENT '记录日期（业务日，GMT+8）',
    `weight`      DECIMAL(6,2) NOT NULL COMMENT '体重(kg)',
    `bmi`         DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'BMI = weight(kg) / height(m)^2',
    `source`      TINYINT      NOT NULL DEFAULT 1 COMMENT '来源：1手动 2导入',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `record_date`),
    KEY `idx_user_date` (`user_id`, `record_date` DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='身体数据表（每日体重）';