package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 减肥目标状态
 */
@Getter
public enum WeightGoalStatusEnum {

    ACTIVE(1, "生效中"),
    ARCHIVED(2, "已归档"),
    ;

    private final Integer status;
    private final String desc;

    WeightGoalStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static WeightGoalStatusEnum of(Integer status) {
        if (status == null) return null;
        for (WeightGoalStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

    public boolean isTerminal() {
        return this == ARCHIVED;
    }
}