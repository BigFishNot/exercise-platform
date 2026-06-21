package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 当日打卡状态 VO
 */
@Data
public class CheckInTodayVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planId;
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date exerciseDate;

    /** 目标分钟 */
    private Integer targetMinutes;
    /** 实际累计分钟（DONE 记录之和 / 60） */
    private Integer actualMinutes;
    /** 完成率（0-100） */
    private Integer completionRate;
    /** 状态：1 DONE / 2 INSUFFICIENT / 3 NOT_DONE */
    private Integer status;
    private String statusName;
}