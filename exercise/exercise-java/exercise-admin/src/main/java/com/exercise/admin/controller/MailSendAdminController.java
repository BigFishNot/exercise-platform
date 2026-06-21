package com.exercise.admin.controller;

import com.exercise.admin.biz.MailSendAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.MailSendRequestDTO;
import com.exercise.campus.entity.vo.MailSendResultVO;
import com.exercise.campus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mailSend/admin")
public class MailSendAdminController extends BaseController {

    @Autowired
    private MailSendAdminBiz biz;

    @PostMapping("/send")
    public ResponseVO<MailSendResultVO> send(@RequestBody MailSendRequestDTO dto) {
        return success(biz.sendManual(dto));
    }
}