package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.vo.PersonalSummaryVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.StatisticsWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计 Web Controller
 * 路径：/api/statistics/*
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsWebController extends BaseController {

    @Autowired
    private StatisticsWebBiz statisticsWebBiz;

    @GetMapping("/personalSummary")
    public ResponseVO<PersonalSummaryVO> personalSummary() {
        return success(statisticsWebBiz.personalSummary());
    }
}