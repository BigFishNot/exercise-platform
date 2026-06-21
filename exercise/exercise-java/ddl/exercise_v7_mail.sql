-- ============================================================
-- exercise 数据库增量脚本 v7 - 邮件通知系统
--   mail_config: 单例 SMTP 配置 (QQ 邮箱默认)
--   mail_template: 邮件模板 (NOT_DONE / INSUFFICIENT)
--   mail_log: 发送记录 (定时 + 手动)
-- ============================================================

USE `exercise`;

-- ------------------------------------------------------------
-- 邮件配置 (单例, id 固定为 1)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mail_config`;
CREATE TABLE `mail_config` (
    `id`                  INT          NOT NULL DEFAULT 1 COMMENT '固定为 1，单例',
    `smtp_host`           VARCHAR(128) NOT NULL COMMENT 'SMTP 主机，如 smtp.qq.com',
    `smtp_port`           INT          NOT NULL DEFAULT 465 COMMENT 'SMTP 端口，QQ 邮箱 SSL=465',
    `smtp_username`       VARCHAR(128) NOT NULL COMMENT 'SMTP 登录账号（QQ 邮箱就是 QQ 号 @qq.com）',
    `smtp_password`       VARCHAR(512) NOT NULL COMMENT 'SMTP 授权码（AES 密文存储）',
    `sender`              VARCHAR(128) DEFAULT NULL COMMENT '发件人显示名',
    `use_ssl`             TINYINT      NOT NULL DEFAULT 1 COMMENT '是否 SSL：1 是 0 否',
    `send_time_points`    VARCHAR(255) DEFAULT NULL COMMENT '每日发送时间点（HH:mm，逗号分隔，1-6 个）',
    `enabled`             TINYINT      NOT NULL DEFAULT 1 COMMENT '总开关：1 启用 0 停用',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='邮件 SMTP 配置（单例）';

-- 默认配置：QQ 邮箱 SMTP（密码留空，admin 后台填入）
INSERT INTO `mail_config` (
    `id`, `smtp_host`, `smtp_port`, `smtp_username`, `smtp_password`, `sender`, `use_ssl`, `send_time_points`, `enabled`
) VALUES (
    1, 'smtp.qq.com', 465, '', '', '运动平台', 1, '09:00,18:00', 0
);

-- ------------------------------------------------------------
-- 邮件模板
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mail_template`;
CREATE TABLE `mail_template` (
    `template_id`   VARCHAR(32)  NOT NULL COMMENT '模板ID（UUID）',
    `template_type` TINYINT      NOT NULL COMMENT '模板分类：1 NOT_DONE 2 INSUFFICIENT',
    `name`          VARCHAR(64)  NOT NULL COMMENT '模板名称（管理端识别用）',
    `title`         VARCHAR(255) NOT NULL COMMENT '邮件标题，支持占位符',
    `content`       TEXT         NOT NULL COMMENT '邮件正文（富文本/纯文本），支持占位符',
    `enabled`       TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用：1 是 0 否',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`template_id`),
    UNIQUE KEY `uk_type` (`template_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='邮件模板表';

-- 默认模板 1: 今日未打卡
INSERT INTO `mail_template` (`template_id`, `template_type`, `name`, `title`, `content`, `enabled`)
VALUES (
    '70000000000000000000000000000001', 1, '今日未打卡提醒',
    '【{nickName}】今天还没打卡哦，{date} 记得动一动～',
    'Hi {nickName}，\n\n今天（{date}）的运动打卡还没看到记录哦。\n\n坚持就是胜利，哪怕只做 5 分钟也算数！快去【运动打卡】记录今天吧 👉\n\n—— 运动平台',
    1
);

-- 默认模板 2: 时长不足
INSERT INTO `mail_template` (`template_id`, `template_type`, `name`, `title`, `content`, `enabled`)
VALUES (
    '70000000000000000000000000000002', 2, '今日时长不足提醒',
    '【{nickName}】今天已运动 {actualMinutes} 分钟，还差 {targetMinutes} 分钟完成目标',
    'Hi {nickName}，\n\n今天已经运动 {actualMinutes} 分钟，距离每日目标 {targetMinutes} 分钟还差一点。\n\n再坚持 5-10 分钟就能打卡成功，加油 💪\n\n—— 运动平台',
    1
);

-- ------------------------------------------------------------
-- 邮件发送日志
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mail_log`;
CREATE TABLE `mail_log` (
    `log_id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`         VARCHAR(32)  NOT NULL COMMENT '接收用户ID',
    `user_email`      VARCHAR(128) DEFAULT NULL COMMENT '接收邮箱（冗余便于追查）',
    `template_id`     VARCHAR(32)  DEFAULT NULL COMMENT '模板ID',
    `template_type`   TINYINT      NOT NULL COMMENT '模板分类：1 NOT_DONE 2 INSUFFICIENT',
    `send_date`       DATE         NOT NULL COMMENT '业务发送日期（GMT+8 当日）',
    `trigger_type`    TINYINT      NOT NULL DEFAULT 1 COMMENT '触发：1 定时 2 手动',
    `operator_id`     VARCHAR(32)  DEFAULT NULL COMMENT '手动发送时的管理员ID',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 成功 2 失败',
    `error_summary`   VARCHAR(512) DEFAULT NULL COMMENT '失败原因摘要',
    `rendered_title`  VARCHAR(255) DEFAULT NULL COMMENT '渲染后的邮件标题（便于追查）',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`log_id`),
    UNIQUE KEY `uk_user_type_date_success` (`user_id`, `template_type`, `send_date`, `status`),
    KEY `idx_user_date` (`user_id`, `send_date`),
    KEY `idx_send_date` (`send_date`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='邮件发送日志';
