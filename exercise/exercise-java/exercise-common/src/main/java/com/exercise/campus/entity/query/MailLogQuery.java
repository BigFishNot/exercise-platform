package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件日志分页查询
 */
@Data
public class MailLogQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;
    private Integer templateType;
    private Integer triggerType;
    private String userAccount;
}