package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.entity.vo.PersonalSummaryVO;
import com.exercise.campus.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计 Web Biz
 */
@Component
public class StatisticsWebBiz {

    @Autowired
    private StatisticsService statisticsService;

    public PersonalSummaryVO personalSummary() {
        String userId = LoginContextHolder.requireUserId();
        return statisticsService.personalSummary(userId);
    }
}