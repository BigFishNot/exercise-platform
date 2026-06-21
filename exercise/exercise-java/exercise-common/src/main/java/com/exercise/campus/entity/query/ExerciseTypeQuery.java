package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 运动类型分页查询参数
 */
@Data
public class ExerciseTypeQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 名称模糊 */
    private String nameFuzzy;
    /** 状态 */
    private Integer status;
}