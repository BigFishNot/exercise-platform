package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.entity.dto.PlanAddDTO;
import com.exercise.campus.entity.dto.PlanUpdateDailyTargetDTO;
import com.exercise.campus.entity.vo.PlanCalendarVO;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.service.ExercisePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 阶段计划 Web Biz
 */
@Component
public class ExercisePlanWebBiz {

    @Autowired
    private ExercisePlanService exercisePlanService;

    public String add(PlanAddDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        return exercisePlanService.add(userId, dto);
    }

    public void updateDailyTarget(PlanUpdateDailyTargetDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        exercisePlanService.updateDailyTarget(userId, dto);
    }

    public void cancel(String planId) {
        String userId = LoginContextHolder.requireUserId();
        exercisePlanService.cancel(userId, planId);
    }

    public PlanVO getCurrent() {
        String userId = LoginContextHolder.requireUserId();
        return exercisePlanService.getCurrent(userId);
    }

    public List<PlanVO> list() {
        String userId = LoginContextHolder.requireUserId();
        return exercisePlanService.listByUser(userId);
    }

    public PlanCalendarVO getCalendar(String planId) {
        String userId = LoginContextHolder.requireUserId();
        return exercisePlanService.getCalendar(userId, planId);
    }
}