package com.exercise.campus.service;

import com.exercise.campus.entity.dto.MailTemplateUpsertDTO;
import com.exercise.campus.entity.po.MailTemplate;
import com.exercise.campus.entity.vo.MailTemplateVO;

import java.util.List;

public interface MailTemplateService {

    List<MailTemplateVO> list();

    MailTemplateVO getById(String templateId);

    MailTemplateVO upsert(MailTemplateUpsertDTO dto);

    void delete(String templateId);

    /** 内部：按 type 取模板（用于发送时） */
    MailTemplate loadByType(Integer templateType);

    /** 内部：按 ID 取模板 PO（用于发送时） */
    MailTemplate loadById(String templateId);
}