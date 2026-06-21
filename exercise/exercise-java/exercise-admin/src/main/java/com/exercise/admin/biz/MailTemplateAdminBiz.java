package com.exercise.admin.biz;

import com.exercise.campus.entity.dto.MailTemplateUpsertDTO;
import com.exercise.campus.entity.vo.MailTemplateVO;
import com.exercise.campus.service.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MailTemplateAdminBiz {

    @Autowired
    private MailTemplateService mailTemplateService;

    public List<MailTemplateVO> list() {
        return mailTemplateService.list();
    }

    public MailTemplateVO getById(String templateId) {
        return mailTemplateService.getById(templateId);
    }

    public MailTemplateVO upsert(MailTemplateUpsertDTO dto) {
        return mailTemplateService.upsert(dto);
    }

    public void delete(String templateId) {
        mailTemplateService.delete(templateId);
    }
}