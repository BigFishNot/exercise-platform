package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 阶段计划状态枚举
 */
@Getter
public enum PlanStatusEnum {

    ONGOING(1, "进行中"),
    FINISHED(2, "已完成"),
    CANCELED(3, "已取消"),
    ;

    private final Integer status;
    private final String desc;

    PlanStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static PlanStatusEnum of(Integer status) {
        if (status == null) return null;
        for (PlanStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

    /** 终态判断 */
    public boolean isTerminal() {
        return this == FINISHED || this == CANCELED;
    }
}