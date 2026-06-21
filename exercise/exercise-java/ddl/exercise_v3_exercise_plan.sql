-- ============================================================
-- exercise 数据库增量脚本 v3 - 阶段计划
-- 关键设计: ongoing_unique = user_id 当 status=ONGOING，否则 NULL
--          UNIQUE(ongoing_unique) 利用 MySQL 多 NULL 不冲突的特性，
--          实现"同一用户最多一个进行中计划"的数据库级约束
-- ============================================================

USE `exercise`;

DROP TABLE IF EXISTS `exercise_plan`;
CREATE TABLE `exercise_plan` (
    `plan_id`              VARCHAR(32)  NOT NULL COMMENT '计划ID（UUID）',
    `user_id`              VARCHAR(32)  NOT NULL COMMENT '所属用户ID',
    `start_date`           DATE         NOT NULL COMMENT '开始日期',
    `end_date`             DATE         NOT NULL COMMENT '结束日期',
    `daily_target_minutes` INT          NOT NULL DEFAULT 30 COMMENT '每日目标时长（分钟，1-600）',
    `remark`               VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status`               TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1进行中 2已完成 3已取消',
    `ongoing_unique`       VARCHAR(32)  DEFAULT NULL COMMENT 'ONGOING 唯一约束占位列：=user_id 当 ONGOING，=NULL 终态',
    `finish_time`          DATETIME     DEFAULT NULL COMMENT '结束时间（FINISHED/CANCELED 时记录）',
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`plan_id`),
    UNIQUE KEY `uk_ongoing` (`ongoing_unique`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_user_date`   (`user_id`, `start_date`, `end_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='阶段计划表';