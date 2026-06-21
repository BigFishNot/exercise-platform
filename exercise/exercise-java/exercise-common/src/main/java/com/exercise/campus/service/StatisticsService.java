package com.exercise.campus.service;

import com.exercise.campus.entity.vo.PersonalSummaryVO;
import com.exercise.campus.entity.vo.PlatformOverviewVO;

/**
 * 统计 Service
 */
public interface StatisticsService {

    /** 用户端：个人汇总 */
    PersonalSummaryVO personalSummary(String userId);

    /** 管理端：平台总览 */
    PlatformOverviewVO platformOverview();
}