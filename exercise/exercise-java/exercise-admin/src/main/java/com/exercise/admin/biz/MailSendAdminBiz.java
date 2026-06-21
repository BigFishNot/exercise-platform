package com.exercise.admin.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.entity.dto.MailSendRequestDTO;
import com.exercise.campus.entity.vo.MailSendResultVO;
import com.exercise.campus.service.MailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailSendAdminBiz {

    @Autowired
    private MailSendService mailSendService;

    public MailSendResultVO sendManual(MailSendRequestDTO dto) {
        String operatorId = LoginContextHolder.requireUserId();
        return mailSendService.sendManual(operatorId, dto);
    }
}