package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 阶段计划 - 日历响应
 */
@Data
public class PlanCalendarVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    private Integer dailyTargetMinutes;

    /** 阶段内每天的判定结果 */
    private List<PlanCalendarDayVO> days;
}