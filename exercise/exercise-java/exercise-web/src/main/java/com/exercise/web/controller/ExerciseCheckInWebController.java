package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.vo.CheckInTodayVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.ExerciseCheckInWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打卡判定 Web Controller
 * 路径：/api/exerciseCheckIn/*
 */
@RestController
@RequestMapping("/exerciseCheckIn")
public class ExerciseCheckInWebController extends BaseController {

    @Autowired
    private ExerciseCheckInWebBiz exerciseCheckInWebBiz;

    @GetMapping("/getToday")
    public ResponseVO<CheckInTodayVO> getToday() {
        return success(exerciseCheckInWebBiz.getToday());
    }
}