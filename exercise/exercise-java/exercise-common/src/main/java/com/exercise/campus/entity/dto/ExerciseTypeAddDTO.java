package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运动类型新增 DTO
 */
@Data
public class ExerciseTypeAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 名称 */
    private String name;
    /** 图标（antd icon 组件名） */
    private String icon;
    /** 默认倒计时（秒） */
    private Integer defaultSeconds;
    /** 可选时长档位 JSON: [{label, seconds}, ...] */
    private String durationLevels;
    /** 每分钟消耗卡路里 */
    private BigDecimal caloriesPerMinute;
    /** 排序权重 */
    private Integer sort;
}