package com.exercise.admin.controller;

import com.exercise.admin.biz.MailLogAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.query.MailLogQuery;
import com.exercise.campus.entity.vo.MailLogVO;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mailLog/admin")
public class MailLogAdminController extends BaseController {

    @Autowired
    private MailLogAdminBiz biz;

    @PostMapping("/loadDataList")
    public ResponseVO<PageResultVO<MailLogVO>> loadDataList(@RequestBody MailLogQuery query) {
        return success(biz.loadDataList(query));
    }
}