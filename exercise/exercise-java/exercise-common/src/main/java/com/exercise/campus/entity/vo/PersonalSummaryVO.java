package com.exercise.campus.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 个人统计汇总 VO
 */
@Data
public class PersonalSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /* === 阶段计划 === */
    private Boolean hasActivePlan;
    private String planId;
    private Date startDate;
    private Date endDate;
    private Integer dailyTargetMinutes;
    /** 阶段内已打卡天数（DONE） */
    private Integer doneDays;
    /** 时长不足天数（INSUFFICIENT） */
    private Integer insufficientDays;
    /** 阶段总天数 */
    private Integer totalDays;
    /** 阶段完成率 0-100 */
    private Integer planCompletionRate;
    /** 剩余天数 */
    private Integer daysRemaining;

    /* === 运动累计 === */
    /** 累计有效运动天数（所有 DONE 记录按日期去重） */
    private Integer totalActiveDays;
    /** 累计有效运动时长（秒） */
    private Long totalActiveSeconds;
    /** 累计消耗卡路里 */
    private BigDecimal totalCalories;
    /** 近 7 天运动天数 */
    private Integer last7DaysActiveDays;
    /** 近 30 天运动天数 */
    private Integer last30DaysActiveDays;
    /** 日均运动时长（秒） = totalActiveSeconds / totalActiveDays */
    private Long avgSecondsPerActiveDay;

    /* === 身体数据 === */
    private BigDecimal latestWeight;
    private BigDecimal latestBmi;
    /** 较 7 天前变化（kg） */
    private BigDecimal weightChange7d;
    private BigDecimal targetWeight;
    /** 距目标还差（kg，=latest - target，正数=还差多少） */
    private BigDecimal distanceToTarget;

    /* === 今日 === */
    private Integer todayCheckInStatus; // 1 DONE / 2 INSUFFICIENT / 3 NOT_DONE
    private String todayCheckInStatusName;
    private Integer todayActualMinutes;
    private Integer todayTargetMinutes;
    private Integer todayCompletionRate;
}