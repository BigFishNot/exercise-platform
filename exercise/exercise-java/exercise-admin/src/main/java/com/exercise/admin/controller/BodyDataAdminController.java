package com.exercise.admin.controller;

import com.exercise.admin.biz.BodyDataAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.query.BodyDataAdminQuery;
import com.exercise.campus.entity.vo.BodyDataVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 身体数据 Admin Controller
 * 路径：/api/bodyData/admin/*
 */
@RestController
@RequestMapping("/bodyData/admin")
public class BodyDataAdminController extends BaseController {

    @Autowired
    private BodyDataAdminBiz bodyDataAdminBiz;

    @PostMapping("/listByUser")
    public ResponseVO<List<BodyDataVO>> listByUser(@RequestBody BodyDataAdminQuery query) {
        Integer limit = query == null || query.getLimit() == null ? 30 : query.getLimit();
        return success(bodyDataAdminBiz.listByUser(query.getUserId(), limit));
    }
}