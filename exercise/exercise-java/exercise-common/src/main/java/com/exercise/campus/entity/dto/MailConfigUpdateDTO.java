package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件配置更新 DTO（密码明文入参，service 内加密落库）
 */
@Data
public class MailConfigUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    /** 明文授权码 */
    private String smtpPassword;
    private String sender;
    private Integer useSsl;
    private String sendTimePoints;
    private Integer enabled;
}