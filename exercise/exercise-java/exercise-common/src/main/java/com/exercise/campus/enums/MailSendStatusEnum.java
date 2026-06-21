package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 邮件发送状态
 */
@Getter
public enum MailSendStatusEnum {

    SUCCESS(1, "成功"),
    FAILED(2, "失败"),
    ;

    private final Integer status;
    private final String desc;

    MailSendStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static MailSendStatusEnum of(Integer status) {
        if (status == null) return null;
        for (MailSendStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }
}