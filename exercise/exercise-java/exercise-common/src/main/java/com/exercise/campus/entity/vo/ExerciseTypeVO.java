package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 运动类型 VO（web 用户端用）
 */
@Data
public class ExerciseTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String typeId;
    private String name;
    private String icon;
    private Integer defaultSeconds;
    private BigDecimal caloriesPerMinute;
    private String statusName;
    /** 时长档位（结构化，前端可直接渲染） */
    private String durationLevels;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}