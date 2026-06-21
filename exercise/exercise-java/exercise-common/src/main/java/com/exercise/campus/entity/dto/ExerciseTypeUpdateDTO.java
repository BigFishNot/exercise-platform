package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运动类型更新 DTO（typeId 必填，其余可改）
 */
@Data
public class ExerciseTypeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String typeId;
    private String name;
    private String icon;
    private Integer defaultSeconds;
    private String durationLevels;
    private BigDecimal caloriesPerMinute;
    private Integer sort;
}