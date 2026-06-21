package com.exercise.admin.controller;

import com.exercise.admin.biz.ExerciseCheckInAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.query.CheckInAdminQuery;
import com.exercise.campus.entity.vo.CheckInAdminVO;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打卡判定 Admin Controller
 * 路径：/api/exerciseCheckIn/admin/*
 */
@RestController
@RequestMapping("/exerciseCheckIn/admin")
public class ExerciseCheckInAdminController extends BaseController {

    @Autowired
    private ExerciseCheckInAdminBiz exerciseCheckInAdminBiz;

    @PostMapping("/loadByUser")
    public ResponseVO<PageResultVO<CheckInAdminVO>> loadByUser(@RequestBody CheckInAdminQuery query) {
        return success(exerciseCheckInAdminBiz.loadByUser(query));
    }
}