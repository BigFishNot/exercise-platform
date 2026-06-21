package com.exercise.admin.controller;

import com.exercise.admin.biz.WeightGoalAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.query.WeightGoalQuery;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 减肥目标 Admin Controller
 * 路径：/api/weightGoal/admin/*
 */
@RestController
@RequestMapping("/weightGoal/admin")
public class WeightGoalAdminController extends BaseController {

    @Autowired
    private WeightGoalAdminBiz weightGoalAdminBiz;

    @PostMapping("/loadByUser")
    public ResponseVO<PageResultVO<WeightGoalVO>> loadByUser(@RequestBody WeightGoalQuery query) {
        return success(weightGoalAdminBiz.loadByUser(query));
    }
}