package com.exercise.campus.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 运动类型 PO（与 exercise_type 表一一映射）
 */
@Data
public class ExerciseType implements Serializable {

    private static final long serialVersionUID = 1L;

    private String typeId;
    private String name;
    private String icon;
    private Integer defaultSeconds;

    /**
     * 存为 JSON 字符串：[{label, seconds}, ...]
     */
    private String durationLevels;

    private BigDecimal caloriesPerMinute;
    private Integer sort;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}