package com.exercise.campus.converter;

import com.exercise.campus.entity.dto.MailConfigUpdateDTO;
import com.exercise.campus.entity.dto.MailTemplateUpsertDTO;
import com.exercise.campus.entity.po.MailConfig;
import com.exercise.campus.entity.po.MailLog;
import com.exercise.campus.entity.po.MailTemplate;
import com.exercise.campus.entity.vo.MailConfigVO;
import com.exercise.campus.entity.vo.MailLogVO;
import com.exercise.campus.entity.vo.MailTemplateVO;
import com.exercise.campus.enums.MailSendStatusEnum;
import com.exercise.campus.enums.MailTemplateTypeEnum;
import com.exercise.campus.enums.MailTriggerTypeEnum;

import java.util.UUID;

/**
 * 邮件模块 Converter
 */
public class MailConverter {

    public static MailConfig toPO(MailConfigUpdateDTO dto, String encryptedPassword) {
        MailConfig po = new MailConfig();
        po.setId(1);
        po.setSmtpHost(dto.getSmtpHost());
        po.setSmtpPort(dto.getSmtpPort() == null ? 465 : dto.getSmtpPort());
        po.setSmtpUsername(dto.getSmtpUsername());
        po.setSmtpPassword(encryptedPassword);
        po.setSender(dto.getSender());
        po.setUseSsl(dto.getUseSsl() == null ? 1 : dto.getUseSsl());
        po.setSendTimePoints(dto.getSendTimePoints());
        po.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
        return po;
    }

    public static MailConfigVO toConfigVO(MailConfig po) {
        if (po == null) return null;
        MailConfigVO vo = new MailConfigVO();
        vo.setId(po.getId());
        vo.setSmtpHost(po.getSmtpHost());
        vo.setSmtpPort(po.getSmtpPort());
        vo.setSmtpUsername(po.getSmtpUsername());
        vo.setPasswordConfigured(po.getSmtpPassword() != null && !po.getSmtpPassword().isEmpty());
        vo.setSmtpPasswordMasked(vo.getPasswordConfigured() ? "********" : "");
        vo.setSender(po.getSender());
        vo.setUseSsl(po.getUseSsl());
        vo.setSendTimePoints(po.getSendTimePoints());
        vo.setEnabled(po.getEnabled());
        vo.setUpdateTime(po.getUpdateTime());
        return vo;
    }

    public static MailTemplate toTemplatePO(MailTemplateUpsertDTO dto) {
        MailTemplate po = new MailTemplate();
        po.setTemplateId(dto.getTemplateId() != null && !dto.getTemplateId().isEmpty()
                ? dto.getTemplateId()
                : UUID.randomUUID().toString().replace("-", ""));
        po.setTemplateType(dto.getTemplateType());
        po.setName(dto.getName());
        po.setTitle(dto.getTitle());
        po.setContent(dto.getContent());
        po.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());
        return po;
    }

    public static MailTemplateVO toTemplateVO(MailTemplate po) {
        if (po == null) return null;
        MailTemplateVO vo = new MailTemplateVO();
        vo.setTemplateId(po.getTemplateId());
        vo.setTemplateType(po.getTemplateType());
        vo.setTypeName(po.getTemplateType() != null
                ? MailTemplateTypeEnum.of(po.getTemplateType()).getDesc() : null);
        vo.setName(po.getName());
        vo.setTitle(po.getTitle());
        vo.setContent(po.getContent());
        vo.setEnabled(po.getEnabled());
        vo.setUpdateTime(po.getUpdateTime());
        return vo;
    }

    public static MailLogVO toLogVO(MailLog po) {
        if (po == null) return null;
        MailLogVO vo = new MailLogVO();
        vo.setLogId(po.getLogId());
        vo.setUserId(po.getUserId());
        vo.setUserEmail(po.getUserEmail());
        vo.setTemplateId(po.getTemplateId());
        vo.setTemplateType(po.getTemplateType());
        vo.setTypeName(po.getTemplateType() != null
                ? MailTemplateTypeEnum.of(po.getTemplateType()).getDesc() : null);
        vo.setTriggerType(po.getTriggerType());
        vo.setTriggerName(po.getTriggerType() != null
                ? MailTriggerTypeEnum.of(po.getTriggerType()).getDesc() : null);
        vo.setOperatorId(po.getOperatorId());
        vo.setStatus(po.getStatus());
        vo.setStatusName(po.getStatus() != null
                ? MailSendStatusEnum.of(po.getStatus()).getDesc() : null);
        vo.setErrorSummary(po.getErrorSummary());
        vo.setRenderedTitle(po.getRenderedTitle());
        vo.setSendDate(po.getSendDate());
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }
}