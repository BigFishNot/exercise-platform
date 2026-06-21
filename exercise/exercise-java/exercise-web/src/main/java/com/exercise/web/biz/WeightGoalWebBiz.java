package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.entity.dto.WeightGoalAddDTO;
import com.exercise.campus.entity.dto.WeightGoalIdDTO;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.service.WeightGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 减肥目标 Web Biz
 */
@Component
public class WeightGoalWebBiz {

    @Autowired
    private WeightGoalService weightGoalService;

    public String add(WeightGoalAddDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        return weightGoalService.add(userId, dto);
    }

    public void update(WeightGoalAddDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        weightGoalService.update(userId, dto);
    }

    public void archive(WeightGoalIdDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        weightGoalService.archive(userId, dto);
    }

    public WeightGoalVO getActive() {
        String userId = LoginContextHolder.requireUserId();
        return weightGoalService.getActive(userId);
    }

    public List<WeightGoalVO> list() {
        String userId = LoginContextHolder.requireUserId();
        return weightGoalService.listByUser(userId);
    }
}