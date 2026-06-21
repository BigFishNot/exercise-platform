package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
public enum GenderEnum {

    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女"),
    ;

    private final Integer gender;
    private final String desc;

    GenderEnum(Integer gender, String desc) {
        this.gender = gender;
        this.desc = desc;
    }

    public static GenderEnum of(Integer gender) {
        if (gender == null) {
            return null;
        }
        for (GenderEnum value : values()) {
            if (value.gender.equals(gender)) {
                return value;
            }
        }
        return null;
    }
}