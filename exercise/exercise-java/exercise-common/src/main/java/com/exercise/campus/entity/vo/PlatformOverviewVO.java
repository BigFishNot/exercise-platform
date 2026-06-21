package com.exercise.campus.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 平台总览 VO（admin）
 */
@Data
public class PlatformOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /* === 用户 === */
    private Long totalUsers;
    private Long enabledUsers;
    private Long newUsersToday;
    private Long newUsers7d;
    private Long newUsers30d;
    /** 近 7 天有运动记录的用户数 */
    private Long activeUsers7d;

    /* === 运动 === */
    private Long totalRecords;
    private Long todayRecordCount;
    private Long todayDoneCount;
    private Long records7d;
    private Long records30d;
    /** 全平台累计有效运动总秒数 */
    private Long totalActiveSeconds;
    /** 全平台累计消耗卡路里 */
    private java.math.BigDecimal totalCalories;

    /* === 阶段计划 === */
    private Long ongoingPlans;

    /* === 邮件（留位，mail 模块未做时为 0） === */
    private Long mailSent7d;
    private Long mailSentSuccess7d;
    private Integer mailSuccessRate7d; // 0-100

    /* === 热门运动类型 Top 5 === */
    private List<HotExerciseTypeVO> topExerciseTypes;
}