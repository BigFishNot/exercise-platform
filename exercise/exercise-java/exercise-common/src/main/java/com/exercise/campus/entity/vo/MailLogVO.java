package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件日志 VO
 */
@Data
public class MailLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long logId;
    private String userId;
    private String userAccount;
    private String userEmail;
    private String templateId;
    private Integer templateType;
    private String typeName;
    private Integer triggerType;
    private String triggerName;
    private String operatorId;
    private String operatorAccount;
    private Integer status;
    private String statusName;
    private String errorSummary;
    private String renderedTitle;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date sendDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}