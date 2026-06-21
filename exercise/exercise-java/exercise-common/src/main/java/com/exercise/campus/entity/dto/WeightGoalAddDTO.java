package com.exercise.campus.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 减肥目标新增 DTO
 * 日期字段为 yyyy-MM-dd 字符串，与 PO 端 Date 类型对齐
 */
@Data
public class WeightGoalAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal targetWeight;
    private BigDecimal targetBodyFat;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date targetDate;

    private String remark;
}