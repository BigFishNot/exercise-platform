package com.exercise.admin.biz;

import com.exercise.campus.entity.dto.MailConfigUpdateDTO;
import com.exercise.campus.entity.vo.MailConfigVO;
import com.exercise.campus.service.MailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailConfigAdminBiz {

    @Autowired
    private MailConfigService mailConfigService;

    public MailConfigVO get() {
        return mailConfigService.get();
    }

    public void update(MailConfigUpdateDTO dto) {
        mailConfigService.update(dto);
    }
}