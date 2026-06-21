package com.exercise.campus.enums;

import lombok.Getter;

/**
 * 系统角色枚举
 */
@Getter
public enum RoleTypeEnum {

    ADMIN(1, "系统管理员"),
    USER(2, "普通用户"),
    ;

    private final Integer roleType;
    private final String desc;

    RoleTypeEnum(Integer roleType, String desc) {
        this.roleType = roleType;
        this.desc = desc;
    }

    public static RoleTypeEnum of(Integer roleType) {
        if (roleType == null) {
            return null;
        }
        for (RoleTypeEnum value : values()) {
            if (value.roleType.equals(roleType)) {
                return value;
            }
        }
        return null;
    }
}