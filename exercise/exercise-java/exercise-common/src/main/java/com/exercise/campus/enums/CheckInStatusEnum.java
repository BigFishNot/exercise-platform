package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 打卡状态枚举
 */
@Getter
public enum CheckInStatusEnum {

    DONE(1, "已打卡"),
    INSUFFICIENT(2, "时长不足"),
    NOT_DONE(3, "未打卡"),
    FUTURE(4, "未来"),
    ;

    private final Integer status;
    private final String desc;

    CheckInStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static CheckInStatusEnum of(Integer status) {
        if (status == null) return null;
        for (CheckInStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }
}