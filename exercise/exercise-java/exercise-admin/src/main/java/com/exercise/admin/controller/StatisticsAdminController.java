package com.exercise.admin.controller;

import com.exercise.admin.biz.StatisticsAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.vo.PlatformOverviewVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计 Admin Controller
 * 路径：/api/statistics/admin/*
 */
@RestController
@RequestMapping("/statistics/admin")
public class StatisticsAdminController extends BaseController {

    @Autowired
    private StatisticsAdminBiz statisticsAdminBiz;

    @GetMapping("/platformOverview")
    public ResponseVO<PlatformOverviewVO> platformOverview() {
        return success(statisticsAdminBiz.platformOverview());
    }
}