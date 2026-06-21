-- ============================================================
-- exercise 数据库初始化脚本
-- 适用 MySQL 8.x，字符集 utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `exercise`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `exercise`;

-- ------------------------------------------------------------
-- 用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
    `user_id`          VARCHAR(32)   NOT NULL COMMENT '用户ID（UUID）',
    `account`          VARCHAR(64)   NOT NULL COMMENT '账号（手机号 / 邮箱）',
    `email`            VARCHAR(128)  DEFAULT NULL COMMENT '邮箱',
    `nick_name`        VARCHAR(64)   NOT NULL COMMENT '昵称',
    `password`         VARCHAR(128)  NOT NULL COMMENT '密码（BCrypt）',
    `avatar`           VARCHAR(255)  DEFAULT NULL COMMENT '头像',
    `gender`           TINYINT       DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `birth_date`       DATE          DEFAULT NULL COMMENT '出生年月',
    `height`           DECIMAL(6,2)  DEFAULT NULL COMMENT '身高(cm)',
    `weight`           DECIMAL(6,2)  DEFAULT NULL COMMENT '体重(kg)',
    `role_type`        TINYINT       NOT NULL DEFAULT 2 COMMENT '角色：1管理员 2普通用户',
    `status`           TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：0停用 1正常',
    `register_time`    DATETIME      DEFAULT NULL COMMENT '注册时间',
    `last_login_time`  DATETIME      DEFAULT NULL COMMENT '最近登录时间',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_account` (`account`),
    UNIQUE KEY `uk_email`   (`email`),
    KEY `idx_role_status` (`role_type`, `status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- ------------------------------------------------------------
-- 初始数据：超管账号
-- 账号：admin
-- 密码：admin123 （BCrypt）
-- 首次登录后强制改密（业务侧要求）
-- ------------------------------------------------------------
INSERT INTO `user_info`
    (`user_id`, `account`, `email`, `nick_name`, `password`, `gender`, `role_type`, `status`, `register_time`)
VALUES
    ('00000000000000000000000000000001',
     'admin',
     'admin@exercise.local',
     '超级管理员',
     '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
     1, 1, 1, NOW());