package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶段计划 VO（用户端 / 通用）
 */
@Data
public class PlanVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planId;
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    private Integer dailyTargetMinutes;
    private String remark;
    private Integer status;
    private String statusName;

    /** 阶段总天数（含首尾） */
    private Integer daysTotal;
    /** 已进行天数（按服务器当日计算） */
    private Integer daysElapsed;
    /** 剩余天数 */
    private Integer daysRemaining;
    /** 当日累计有效运动时长（分钟），0 表示尚未记录（exerciseRecord 模块接入后回填） */
    private Integer todayActualMinutes;
    /** 当日是否完成（基于 todayActualMinutes >= dailyTargetMinutes），未接 checkIn 前为 false */
    private Boolean todayDone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}