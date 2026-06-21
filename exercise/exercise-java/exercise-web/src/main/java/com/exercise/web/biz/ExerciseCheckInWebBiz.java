package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.entity.vo.CheckInTodayVO;
import com.exercise.campus.service.ExerciseCheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 打卡判定 Web Biz
 */
@Component
public class ExerciseCheckInWebBiz {

    @Autowired
    private ExerciseCheckInService exerciseCheckInService;

    public CheckInTodayVO getToday() {
        String userId = LoginContextHolder.requireUserId();
        return exerciseCheckInService.getToday(userId);
    }
}