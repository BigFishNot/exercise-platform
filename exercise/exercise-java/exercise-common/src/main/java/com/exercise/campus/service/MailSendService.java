package com.exercise.campus.service;

import com.exercise.campus.entity.dto.MailSendRequestDTO;
import com.exercise.campus.entity.vo.MailSendResultVO;

import java.util.Map;

public interface MailSendService {

    /**
     * 管理员手动发送
     * @param operatorId 管理员 ID
     * @param dto 请求
     * @return 发送结果
     */
    MailSendResultVO sendManual(String operatorId, MailSendRequestDTO dto);

    /**
     * 渲染模板（替换占位符）
     * @param template 模板（title/content 必填）
     * @param vars 占位符变量
     * @return [renderedTitle, renderedContent]
     */
    String[] render(com.exercise.campus.entity.po.MailTemplate template, Map<String, String> vars);
}