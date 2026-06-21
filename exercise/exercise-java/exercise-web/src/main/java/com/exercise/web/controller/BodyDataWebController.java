package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.BodyDataAddDTO;
import com.exercise.campus.entity.query.BodyDataTrendQuery;
import com.exercise.campus.entity.vo.BodyDataVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.BodyDataWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 身体数据 Web Controller
 * 路径：/api/bodyData/*
 */
@RestController
@RequestMapping("/bodyData")
public class BodyDataWebController extends BaseController {

    @Autowired
    private BodyDataWebBiz bodyDataWebBiz;

    @PostMapping("/upsert")
    public ResponseVO<Long> upsert(@RequestBody BodyDataAddDTO dto) {
        return success(bodyDataWebBiz.upsert(dto));
    }

    @GetMapping("/getToday")
    public ResponseVO<BodyDataVO> getToday() {
        return success(bodyDataWebBiz.getToday());
    }

    @GetMapping("/getById")
    public ResponseVO<BodyDataVO> getById(Long id) {
        return success(bodyDataWebBiz.getById(id));
    }

    @PostMapping("/delete")
    public ResponseVO<Void> delete(@RequestBody BodyDataAddDTO dto) {
        bodyDataWebBiz.delete(dto.getRecordDate() == null ? null
                : Long.valueOf(String.valueOf(dto.getRecordDate())));
        return success();
    }

    @PostMapping("/getTrend")
    public ResponseVO<BodyDataWebBiz.TrendVO> getTrend(@RequestBody BodyDataTrendQuery query) {
        return success(bodyDataWebBiz.getTrend(query));
    }
}