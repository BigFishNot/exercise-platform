package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改每日目标 DTO（仅允许改 dailyTargetMinutes + remark）
 */
@Data
public class PlanUpdateDailyTargetDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planId;
    private Integer dailyTargetMinutes;
    private String remark;
}