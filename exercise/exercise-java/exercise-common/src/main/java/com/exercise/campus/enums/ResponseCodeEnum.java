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