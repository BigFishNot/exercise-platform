package com.exercise.admin.controller;

import com.exercise.admin.biz.ExercisePlanAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.query.ExercisePlanQuery;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 阶段计划 Admin Controller
 * 路径：/api/exercisePlan/admin/*
 */
@RestController
@RequestMapping("/exercisePlan/admin")
public class ExercisePlanAdminController extends BaseController {

    @Autowired
    private ExercisePlanAdminBiz exercisePlanAdminBiz;

    @PostMapping("/loadByUser")
    public ResponseVO<PageResultVO<PlanVO>> loadByUser(@RequestBody ExercisePlanQuery query) {
        return success(exercisePlanAdminBiz.loadByUser(query));
    }

    @GetMapping("/detail/{planId}")
    public ResponseVO<PlanVO> detail(@PathVariable("planId") String planId) {
        return success(exercisePlanAdminBiz.detail(planId));
    }
}