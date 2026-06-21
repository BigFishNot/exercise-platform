-- ============================================================
-- exercise 数据库增量脚本 v6 - 减肥目标
-- 设计与 exercise_plan 同构:
--   - active_marker = user_id 当 ACTIVE, NULL 当 ARCHIVED
--   - UNIQUE(active_marker) 利用 MySQL 多 NULL 不冲突特性, 实现"同用户最多 1 个 ACTIVE"
-- ============================================================

USE `exercise`;

DROP TABLE IF EXISTS `weight_goal`;
CREATE TABLE `weight_goal` (
    `goal_id`           VARCHAR(32)  NOT NULL COMMENT '目标ID（UUID）',
    `user_id`           VARCHAR(32)  NOT NULL COMMENT '所属用户ID',
    `target_weight`     DECIMAL(6,2) NOT NULL COMMENT '目标体重(kg)',
    `target_body_fat`   DECIMAL(5,2) DEFAULT NULL COMMENT '目标体脂率(%)，可选',
    `target_date`       DATE         NOT NULL COMMENT '目标日期',
    `remark`            VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status`            TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1生效中 2已归档',
    `active_marker`     VARCHAR(32)  DEFAULT NULL COMMENT '生效中唯一约束占位列：=user_id 当 ACTIVE，=NULL 归档',
    `archive_time`      DATETIME     DEFAULT NULL COMMENT '归档时间',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`goal_id`),
    UNIQUE KEY `uk_active` (`active_marker`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_user_date`   (`user_id`, `target_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='减肥目标表';