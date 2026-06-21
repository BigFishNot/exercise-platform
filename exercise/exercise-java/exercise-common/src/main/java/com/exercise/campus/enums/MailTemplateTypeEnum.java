package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 邮件模板分类
 */
@Getter
public enum MailTemplateTypeEnum {

    NOT_DONE(1, "今日未打卡"),
    INSUFFICIENT(2, "今日时长不足"),
    ;

    private final Integer type;
    private final String desc;

    MailTemplateTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MailTemplateTypeEnum of(Integer type) {
        if (type == null) return null;
        for (MailTemplateTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}