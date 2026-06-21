package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 响应码枚举
 * 统一维护所有业务错误码
 */
@Getter
public enum ResponseCodeEnum {

    SUCCESS(200, "success", "成功"),
    SYSTEM_ERROR(500, "fail", "系统异常"),
    PARAM_ERROR(400, "fail", "参数错误"),
    BUSINESS_ERROR(501, "fail", "业务异常"),

    UNAUTHORIZED(401, "unauthorized", "未登录或登录已过期"),
    FORBIDDEN(403, "forbidden", "无权限访问"),

    USER_ALREADY_EXISTS(1001, "fail", "账号或邮箱已被注册"),
    USER_NOT_EXISTS(1002, "fail", "用户不存在"),
    PASSWORD_ERROR(1003, "fail", "账号或密码错误"),
    USER_DISABLED(1004, "fail", "账号已被停用"),
    PASSWORD_WEAK(1005, "fail", "密码必须同时包含字母和数字，长度 8-32 位"),
    NICKNAME_INVALID(1006, "fail", "昵称长度需为 2-20 字符"),

    ROLE_NOT_MATCH(2001, "fail", "角色与登录入口不匹配"),

    /* ========== 运动类型 ========== */
    EXERCISE_TYPE_NOT_EXISTS(2101, "fail", "运动类型不存在"),
    EXERCISE_TYPE_NAME_DUPLICATE(2102, "fail", "运动类型名称重复"),
    EXERCISE_TYPE_REFERENCED(2103, "fail", "该运动类型已被运动记录引用，无法删除"),
    EXERCISE_TYPE_PARAM_INVALID(2104, "fail", "运动类型参数不合法"),

    /* ========== 阶段计划 ========== */
    PLAN_NOT_EXISTS(2201, "fail", "阶段计划不存在"),
    PLAN_DATE_INVALID(2202, "fail", "开始日期需 ≥ 当日，且结束日期 > 开始日期，区间 1-60 天"),
    PLAN_TARGET_INVALID(2203, "fail", "每日目标时长需在 1-600 分钟之间"),
    PLAN_HAS_ONGOING(2204, "fail", "已存在进行中的阶段计划，请先取消或结束当前计划"),
    PLAN_NOT_ONGOING(2205, "fail", "当前计划不在进行中状态，无法操作"),
    PLAN_DATE_LOCKED(2206, "fail", "计划已开始，日期区间不可修改"),

    /* ========== 运动记录 ========== */
    RECORD_NOT_EXISTS(2301, "fail", "运动记录不存在"),
    RECORD_TYPE_DISABLED(2302, "fail", "该运动类型已停用，无法开始"),
    RECORD_HAS_ACTIVE(2303, "fail", "已存在进行中的运动记录，请先结束或放弃"),
    RECORD_NOT_IN_PROGRESS(2304, "fail", "当前记录不在进行中状态，无法完成/放弃"),
    RECORD_DURATION_INVALID(2305, "fail", "时长参数不合法，需在 10-7200 秒之间"),

    /* ========== 打卡判定 ========== */
    CHECK_IN_NOT_EXISTS(2401, "fail", "打卡记录不存在"),
    CHECK_IN_NO_PLAN(2402, "fail", "当前用户没有进行中的阶段计划"),

    /* ========== 身体数据 ========== */
    BODY_DATA_WEIGHT_INVALID(2501, "fail", "体重需在 0-500 kg 之间"),
    BODY_DATA_HEIGHT_REQUIRED(2502, "fail", "请先在个人资料设置身高"),

    /* ========== 减肥目标 ========== */
    GOAL_NOT_EXISTS(2601, "fail", "减肥目标不存在"),
    GOAL_WEIGHT_INVALID(2602, "fail", "目标体重需在 0-500 kg 之间"),
    GOAL_DATE_INVALID(2603, "fail", "目标日期需 ≥ 当日"),
    GOAL_BODY_FAT_INVALID(2604, "fail", "目标体脂率需在 0-100% 之间"),
    GOAL_HAS_ACTIVE(2605, "fail", "已存在生效中的目标，请先归档当前目标"),
    GOAL_NOT_ACTIVE(2606, "fail", "当前目标不在生效中状态，无法操作"),

    /* ========== AI 鼓励 ========== */
    AI_ENCOURAGE_LIMIT_EXCEEDED(2701, "fail", "今日鼓励次数已用完（每天最多 3 次），明天再来吧～"),
    AI_ENCOURAGE_FAILED(2702, "fail", "生成鼓励语失败"),
    ;

    private final Integer code;
    private final String status;
    private final String info;

    ResponseCodeEnum(Integer code, String status, String info) {
        this.code = code;
        this.status = status;
        this.info = info;
    }
}