package com.exercise.campus.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 邮件发送结果 VO
 */
@Data
public class MailSendResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 实际发送成功数 */
    private Integer sentCount;
    /** 跳过的用户数（今日已发等） */
    private Integer skippedCount;
    /** 发送失败数 */
    private Integer failedCount;
    /** 跳过详情 */
    private List<SkippedUser> skipped;
    /** 失败详情 */
    private List<FailedUser> failed;

    @Data
    public static class SkippedUser implements Serializable {
        private static final long serialVersionUID = 1L;
        private String userId;
        private String account;
        private String reason;
    }

    @Data
    public static class FailedUser implements Serializable {
        private static final long serialVersionUID = 1L;
        private String userId;
        private String account;
        private String error;
    }
}