package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶段计划 - 日历每一天
 */
@Data
public class PlanCalendarDayVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    /** NOT_DONE / INSUFFICIENT / DONE；exerciseCheckIn 接入后填充 */
    private String checkInStatus;

    /** 目标时长（分钟） */
    private Integer targetMinutes;

    /** 实际累计时长（分钟） */
    private Integer actualMinutes;
}