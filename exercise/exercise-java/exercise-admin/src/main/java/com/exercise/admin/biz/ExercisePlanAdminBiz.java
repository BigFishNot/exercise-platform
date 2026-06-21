package com.exercise.admin.biz;

import com.exercise.campus.entity.query.ExercisePlanQuery;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.service.ExercisePlanService;
import com.exercise.campus.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 阶段计划 Admin Biz
 */
@Component
public class ExercisePlanAdminBiz {

    @Autowired
    private ExercisePlanService exercisePlanService;

    public PageResultVO<PlanVO> loadByUser(ExercisePlanQuery query) {
        return exercisePlanService.pageList(query);
    }

    public PlanVO detail(String planId) {
        return exercisePlanService.detail(planId);
    }
}