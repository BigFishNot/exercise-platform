package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.AiEncourageWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 鼓励 Web Controller
 * 路径：/api/aiEncourage/*
 */
@RestController
@RequestMapping("/aiEncourage")
public class AiEncourageWebController extends BaseController {

    @Autowired
    private AiEncourageWebBiz aiEncourageWebBiz;

    /**
     * 生成鼓励语（GET 简化调用；限频每天 3 次）
     */
    @GetMapping("/generate")
    public ResponseVO<String> generate() {
        return success(aiEncourageWebBiz.generate());
    }
}