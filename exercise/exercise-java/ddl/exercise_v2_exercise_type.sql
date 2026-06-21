-- ============================================================
-- exercise 数据库增量脚本 v2 - 运动类型
-- 在已初始化 exercise.sql 的库上执行本文件
-- ============================================================

USE `exercise`;

-- ------------------------------------------------------------
-- 运动类型表
-- 字段命名遵循 spec:
--   - duration_levels: JSON 数组 [{label, seconds}]
--   - status: 0停用 1正常
--   - sort: 排序权重, 升序
--   - icon: ant-design-vue 图标组件名（前端映射）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `exercise_type`;
CREATE TABLE `exercise_type` (
    `type_id`             VARCHAR(32)  NOT NULL COMMENT '类型ID（UUID）',
    `name`                VARCHAR(64)  NOT NULL COMMENT '名称',
    `icon`                VARCHAR(64)  DEFAULT NULL COMMENT '图标（antd 图标名）',
    `default_seconds`     INT          NOT NULL DEFAULT 60 COMMENT '默认倒计时（秒）',
    `duration_levels`     JSON         DEFAULT NULL COMMENT '可选时长档位: [{label, seconds}]',
    `calories_per_minute` DECIMAL(6,2) NOT NULL DEFAULT 0 COMMENT '每分钟消耗卡路里',
    `sort`                INT          NOT NULL DEFAULT 0 COMMENT '排序权重（升序）',
    `status`              TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0停用 1正常',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`type_id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_status_sort` (`status`, `sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='运动类型表';

-- ------------------------------------------------------------
-- 种子数据：9 个室内运动类型
-- 顺序按 sort 升序
-- ------------------------------------------------------------
INSERT INTO `exercise_type`
    (`type_id`, `name`, `icon`, `default_seconds`, `duration_levels`, `calories_per_minute`, `sort`, `status`)
VALUES
    ('10000000000000000000000000000001', '自由运动', 'ThunderboltOutlined',  1800,
     JSON_ARRAY(JSON_OBJECT('label','15min',  'seconds', 900),
                JSON_OBJECT('label','30min',  'seconds', 1800),
                JSON_OBJECT('label','45min',  'seconds', 2700),
                JSON_OBJECT('label','60min',  'seconds', 3600)),
     4.00,  1, 1),

    ('10000000000000000000000000000002', '跳绳',    'RocketOutlined',       600,
     JSON_ARRAY(JSON_OBJECT('label','1min',  'seconds', 60),
                JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300),
                JSON_OBJECT('label','10min', 'seconds', 600)),
     12.00, 2, 1),

    ('10000000000000000000000000000003', '俯卧撑',  'RiseOutlined',         60,
     JSON_ARRAY(JSON_OBJECT('label','1min',  'seconds', 60),
                JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300)),
     8.00,  3, 1),

    ('10000000000000000000000000000004', '平板支撑','PauseOutlined',        90,
     JSON_ARRAY(JSON_OBJECT('label','1min',  'seconds', 60),
                JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300)),
     4.00,  4, 1),

    ('10000000000000000000000000000005', '仰卧起坐','ReloadOutlined',       120,
     JSON_ARRAY(JSON_OBJECT('label','1min',  'seconds', 60),
                JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300)),
     8.00,  5, 1),

    ('10000000000000000000000000000006', '深蹲',    'CaretUpOutlined',      120,
     JSON_ARRAY(JSON_OBJECT('label','1min',  'seconds', 60),
                JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300)),
     9.00,  6, 1),

    ('10000000000000000000000000000007', '开合跳',  'SwapOutlined',         120,
     JSON_ARRAY(JSON_OBJECT('label','1min',  'seconds', 60),
                JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300)),
     10.00, 7, 1),

    ('10000000000000000000000000000008', '卷腹',    'CompressOutlined',     120,
     JSON_ARRAY(JSON_OBJECT('label','1min',  'seconds', 60),
                JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300)),
     6.00,  8, 1),

    ('10000000000000000000000000000009', '拉伸',    'ExpandOutlined',       300,
     JSON_ARRAY(JSON_OBJECT('label','3min',  'seconds', 180),
                JSON_OBJECT('label','5min',  'seconds', 300),
                JSON_OBJECT('label','10min', 'seconds', 600)),
     3.00,  9, 1);