package com.exercise.campus.service;

import com.exercise.campus.entity.dto.MailConfigUpdateDTO;
import com.exercise.campus.entity.vo.MailConfigVO;

public interface MailConfigService {

    MailConfigVO get();

    void update(MailConfigUpdateDTO dto);

    /** 内部：获取 PO（带密文密码，用于实际发送） */
    com.exercise.campus.entity.po.MailConfig loadPO();
}