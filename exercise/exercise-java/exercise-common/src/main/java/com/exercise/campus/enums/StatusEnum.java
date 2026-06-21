package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 通用启用/停用状态枚举
 */
@Getter
public enum StatusEnum {

    DISABLED(0, "停用"),
    ENABLED(1, "正常"),
    ;

    private final Integer status;
    private final String desc;

    StatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static StatusEnum of(Integer status) {
        if (status == null) {
            return null;
        }
        for (StatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }
}