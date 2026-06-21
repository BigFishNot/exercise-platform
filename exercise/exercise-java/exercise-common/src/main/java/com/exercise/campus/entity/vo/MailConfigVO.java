package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件配置 VO（密码不回显密文，只返回是否已配置）
 */
@Data
public class MailConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    /** 密码不回显（占位） */
    private String smtpPasswordMasked;
    private Boolean passwordConfigured;
    private String sender;
    private Integer useSsl;
    private String sendTimePoints;
    private Integer enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}