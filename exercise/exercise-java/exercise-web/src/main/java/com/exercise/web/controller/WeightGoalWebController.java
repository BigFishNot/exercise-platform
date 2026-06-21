package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.WeightGoalAddDTO;
import com.exercise.campus.entity.dto.WeightGoalIdDTO;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.WeightGoalWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 减肥目标 Web Controller
 * 路径：/api/weightGoal/*
 */
@RestController
@RequestMapping("/weightGoal")
public class WeightGoalWebController extends BaseController {

    @Autowired
    private WeightGoalWebBiz weightGoalWebBiz;

    @PostMapping("/add")
    public ResponseVO<String> add(@RequestBody WeightGoalAddDTO dto) {
        return success(weightGoalWebBiz.add(dto));
    }

    @PostMapping("/update")
    public ResponseVO<Void> update(@RequestBody WeightGoalAddDTO dto) {
        weightGoalWebBiz.update(dto);
        return success();
    }

    @PostMapping("/archive")
    public ResponseVO<Void> archive(@RequestBody WeightGoalIdDTO dto) {
        weightGoalWebBiz.archive(dto);
        return success();
    }

    @GetMapping("/getActive")
    public ResponseVO<WeightGoalVO> getActive() {
        return success(weightGoalWebBiz.getActive());
    }

    @GetMapping("/list")
    public ResponseVO<List<WeightGoalVO>> list() {
        return success(weightGoalWebBiz.list());
    }
}