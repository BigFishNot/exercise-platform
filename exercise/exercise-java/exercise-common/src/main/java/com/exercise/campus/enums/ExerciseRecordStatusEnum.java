package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 运动记录状态枚举
 */
@Getter
public enum ExerciseRecordStatusEnum {

    IN_PROGRESS(1, "进行中"),
    DONE(2, "已完成"),
    ABANDONED(3, "已放弃"),
    ;

    private final Integer status;
    private final String desc;

    ExerciseRecordStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ExerciseRecordStatusEnum of(Integer status) {
        if (status == null) return null;
        for (ExerciseRecordStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

    public boolean isTerminal() {
        return this == DONE || this == ABANDONED;
    }
}