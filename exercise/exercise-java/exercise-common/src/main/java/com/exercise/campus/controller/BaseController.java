package com.exercise.campus.controller;

import com.exercise.campus.vo.ResponseVO;

/**
 * Controller 基类
 * 提供统一的成功 / 失败响应方法，Controller 不要自己 new ResponseVO
 */
public class BaseController {

    protected <T> ResponseVO<T> success() {
        return ResponseVO.ok();
    }

    protected <T> ResponseVO<T> success(T data) {
        return ResponseVO.ok(data);
    }

    protected <T> ResponseVO<T> success(T data, String info) {
        return ResponseVO.ok(data, info);
    }
}