package com.exercise.campus.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶段计划创建 DTO
 * 日期字段为 yyyy-MM-dd 字符串，与 PO 端 Date 类型对齐
 */
@Data
public class PlanAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    private Integer dailyTargetMinutes;
    private String remark;
}