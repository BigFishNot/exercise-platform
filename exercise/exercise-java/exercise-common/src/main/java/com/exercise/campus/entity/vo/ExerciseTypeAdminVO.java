package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 运动类型 Admin VO（管理端列表用，带引用次数等扩展字段）
 */
@Data
public class ExerciseTypeAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String typeId;
    private String name;
    private String icon;
    private Integer defaultSeconds;
    private String durationLevels;
    private BigDecimal caloriesPerMinute;
    private Integer sort;
    private Integer status;
    private String statusName;

    /** 引用次数（被运动记录引用的条数），用于删除前校验 */
    private Integer refCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}