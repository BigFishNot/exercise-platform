package com.exercise.admin.controller;

import com.exercise.admin.biz.MailConfigAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.MailConfigUpdateDTO;
import com.exercise.campus.entity.vo.MailConfigVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mailConfig/admin")
public class MailConfigAdminController extends BaseController {

    @Autowired
    private MailConfigAdminBiz biz;

    @GetMapping("/get")
    public ResponseVO<MailConfigVO> get() {
        return success(biz.get());
    }

    @PostMapping("/update")
    public ResponseVO<Void> update(@RequestBody MailConfigUpdateDTO dto) {
        biz.update(dto);
        return success();
    }
}