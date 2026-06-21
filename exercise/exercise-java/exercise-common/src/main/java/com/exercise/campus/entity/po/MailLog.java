package com.exercise.campus.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件发送日志 PO
 */
@Data
public class MailLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long logId;
    private String userId;
    private String userEmail;
    private String templateId;
    private Integer templateType;
    private Integer triggerType;
    private String operatorId;
    private Integer status;
    private String errorSummary;
    private String renderedTitle;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date sendDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}