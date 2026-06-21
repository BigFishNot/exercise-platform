package com.exercise.campus.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件配置 PO（单例，id 固定为 1）
 */
@Data
public class MailConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    /** AES 密文 */
    private String smtpPassword;
    private String sender;
    private Integer useSsl;
    /** HH:mm 逗号分隔 */
    private String sendTimePoints;
    private Integer enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}