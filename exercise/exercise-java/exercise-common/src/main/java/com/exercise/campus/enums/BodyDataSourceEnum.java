package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 身体数据来源
 */
@Getter
public enum BodyDataSourceEnum {

    MANUAL(1, "手动"),
    IMPORTED(2, "导入"),
    ;

    private final Integer source;
    private final String desc;

    BodyDataSourceEnum(Integer source, String desc) {
        this.source = source;
        this.desc = desc;
    }

    public static BodyDataSourceEnum of(Integer source) {
        if (source == null) return null;
        for (BodyDataSourceEnum value : values()) {
            if (value.source.equals(source)) {
                return value;
            }
        }
        return null;
    }
}