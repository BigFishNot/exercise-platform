package com.exercise.admin.biz;

import com.exercise.campus.entity.query.CheckInAdminQuery;
import com.exercise.campus.entity.vo.CheckInAdminVO;
import com.exercise.campus.service.ExerciseCheckInService;
import com.exercise.campus.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 打卡判定 Admin Biz
 */
@Component
public class ExerciseCheckInAdminBiz {

    @Autowired
    private ExerciseCheckInService exerciseCheckInService;

    public PageResultVO<CheckInAdminVO> loadByUser(CheckInAdminQuery query) {
        return exerciseCheckInService.pageList(query);
    }
}