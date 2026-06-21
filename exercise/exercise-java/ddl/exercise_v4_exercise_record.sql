-- ============================================================
-- exercise 数据库增量脚本 v4 - 运动记录
-- 关键设计:
--   - exercise_date: 业务日期(服务器时区 GMT+8 当日, yyyy-MM-dd)
--   - plan_seconds / actual_seconds: 计划 / 实际秒数
--   - status: 1 进行中 / 2 完成 / 3 放弃
--   - calories: 服务端落库冗余存储(根据实际秒数与运动类型每分钟卡路里计算)
--   - 同一用户同一时刻最多 1 条 IN_PROGRESS: 通过 idx_user_status + 业务层校验保证
-- ============================================================

USE `exercise`;

DROP TABLE IF EXISTS `exercise_record`;
CREATE TABLE `exercise_record` (
    `record_id`       VARCHAR(32)   NOT NULL COMMENT '记录ID（UUID）',
    `user_id`         VARCHAR(32)   NOT NULL COMMENT '所属用户ID',
    `type_id`         VARCHAR(32)   NOT NULL COMMENT '运动类型ID',
    `type_name`       VARCHAR(64)   DEFAULT NULL COMMENT '运动类型名称（冗余，便于历史回显）',
    `plan_seconds`    INT           NOT NULL COMMENT '计划秒数',
    `actual_seconds`  INT           NOT NULL DEFAULT 0 COMMENT '实际秒数',
    `calories`        DECIMAL(8,2)  NOT NULL DEFAULT 0 COMMENT '消耗卡路里',
    `exercise_date`   DATE          NOT NULL COMMENT '运动日期（业务日）',
    `start_time`      DATETIME      NOT NULL COMMENT '开始时间',
    `end_time`        DATETIME      DEFAULT NULL COMMENT '结束时间（IN_PROGRESS 时为 NULL）',
    `status`          TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1进行中 2完成 3放弃',
    `abandon_reason`  VARCHAR(32)   DEFAULT NULL COMMENT '放弃原因：USER_MANUAL / TIMEOUT / UNDER_DURATION',
    `remark`          VARCHAR(255)  DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`record_id`),
    KEY `idx_user_date`   (`user_id`, `exercise_date`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_type`        (`type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='运动记录表';