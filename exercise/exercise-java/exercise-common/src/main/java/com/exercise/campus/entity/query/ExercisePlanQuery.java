package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 阶段计划分页查询参数
 */
@Data
public class ExercisePlanQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID（admin 端按用户筛选时使用） */
    private String userId;
    /** 状态（精确匹配） */
    private Integer status;
    /** 起始日期（>= 过滤，可选） */
    private String startDateFrom;
    /** 截止日期（<= 过滤，可选） */
    private String startDateTo;
}