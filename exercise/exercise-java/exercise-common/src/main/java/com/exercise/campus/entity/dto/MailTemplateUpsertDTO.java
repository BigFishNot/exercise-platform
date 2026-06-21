package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件模板新增/更新 DTO
 */
@Data
public class MailTemplateUpsertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String templateId;
    private Integer templateType;
    private String name;
    private String title;
    private String content;
    private Integer enabled;
}