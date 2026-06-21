package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.vo.ExerciseTypeVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.ExerciseTypeWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 运动类型 Web Controller（用户端）
 * 路径：/api/exerciseType/*
 */
@RestController
@RequestMapping("/exerciseType")
public class ExerciseTypeWebController extends BaseController {

    @Autowired
    private ExerciseTypeWebBiz exerciseTypeWebBiz;

    /**
     * 用户端下拉：仅返回 status=1 的运动类型
     */
    @GetMapping("/getOptions")
    public ResponseVO<List<ExerciseTypeVO>> getOptions() {
        return success(exerciseTypeWebBiz.getOptions());
    }

    /**
     * 详情
     */
    @GetMapping("/detail/{typeId}")
    public ResponseVO<ExerciseTypeVO> detail(@PathVariable("typeId") String typeId) {
        return success(exerciseTypeWebBiz.getDetail(typeId));
    }
}