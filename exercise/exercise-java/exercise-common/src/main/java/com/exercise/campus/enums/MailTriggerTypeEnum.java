package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 邮件触发类型
 */
@Getter
public enum MailTriggerTypeEnum {

    SCHEDULED(1, "定时"),
    MANUAL(2, "手动"),
    ;

    private final Integer type;
    private final String desc;

    MailTriggerTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MailTriggerTypeEnum of(Integer type) {
        if (type == null) return null;
        for (MailTriggerTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}