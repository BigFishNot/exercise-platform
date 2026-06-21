package com.exercise.admin.biz;

import com.exercise.campus.entity.query.WeightGoalQuery;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.service.WeightGoalService;
import com.exercise.campus.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 减肥目标 Admin Biz
 */
@Component
public class WeightGoalAdminBiz {

    @Autowired
    private WeightGoalService weightGoalService;

    public PageResultVO<WeightGoalVO> loadByUser(WeightGoalQuery query) {
        return weightGoalService.pageList(query);
    }
}