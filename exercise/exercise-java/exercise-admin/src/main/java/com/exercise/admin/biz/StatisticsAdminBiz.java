package com.exercise.admin.biz;

import com.exercise.campus.entity.vo.PlatformOverviewVO;
import com.exercise.campus.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计 Admin Biz
 */
@Component
public class StatisticsAdminBiz {

    @Autowired
    private StatisticsService statisticsService;

    public PlatformOverviewVO platformOverview() {
        return statisticsService.platformOverview();
    }
}