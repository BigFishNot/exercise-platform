package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件模板 VO
 */
@Data
public class MailTemplateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String templateId;
    private Integer templateType;
    private String typeName;
    private String name;
    private String title;
    private String content;
    private Integer enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}