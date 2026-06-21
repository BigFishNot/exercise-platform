package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 邮件手动发送 DTO
 */
@Data
public class MailSendRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String templateId;
    private List<String> userIds;
    private String remark;
}