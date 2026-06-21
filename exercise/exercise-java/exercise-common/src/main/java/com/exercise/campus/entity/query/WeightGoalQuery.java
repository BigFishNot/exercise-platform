package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 减肥目标分页查询
 */
@Data
public class WeightGoalQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private Integer status;
}