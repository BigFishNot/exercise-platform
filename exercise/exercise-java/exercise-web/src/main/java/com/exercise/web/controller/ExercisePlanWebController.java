package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.PlanAddDTO;
import com.exercise.campus.entity.dto.PlanIdDTO;
import com.exercise.campus.entity.dto.PlanUpdateDailyTargetDTO;
import com.exercise.campus.entity.vo.PlanCalendarVO;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.ExercisePlanWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 阶段计划 Web Controller（用户端）
 * 路径：/api/exercisePlan/*
 */
@RestController
@RequestMapping("/exercisePlan")
public class ExercisePlanWebController extends BaseController {

    @Autowired
    private ExercisePlanWebBiz exercisePlanWebBiz;

    @PostMapping("/add")
    public ResponseVO<String> add(@RequestBody PlanAddDTO dto) {
        return success(exercisePlanWebBiz.add(dto));
    }

    @PostMapping("/updateDailyTarget")
    public ResponseVO<Void> updateDailyTarget(@RequestBody PlanUpdateDailyTargetDTO dto) {
        exercisePlanWebBiz.updateDailyTarget(dto);
        return success();
    }

    @PostMapping("/cancel")
    public ResponseVO<Void> cancel(@RequestBody PlanIdDTO dto) {
        exercisePlanWebBiz.cancel(dto.getPlanId());
        return success();
    }

    @GetMapping("/getCurrent")
    public ResponseVO<PlanVO> getCurrent() {
        return success(exercisePlanWebBiz.getCurrent());
    }

    @GetMapping("/list")
    public ResponseVO<List<PlanVO>> list() {
        return success(exercisePlanWebBiz.list());
    }

    @GetMapping("/getCalendar/{planId}")
    public ResponseVO<PlanCalendarVO> getCalendar(@PathVariable("planId") String planId) {
        return success(exercisePlanWebBiz.getCalendar(planId));
    }
}