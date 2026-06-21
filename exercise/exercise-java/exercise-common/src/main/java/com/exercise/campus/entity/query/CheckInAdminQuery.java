package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 阶段打卡 - 管理端分页查询
 */
@Data
public class CheckInAdminQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    /** 1 进行中 2 已完成 3 已取消（计划状态） */
    private Integer planStatus;
}