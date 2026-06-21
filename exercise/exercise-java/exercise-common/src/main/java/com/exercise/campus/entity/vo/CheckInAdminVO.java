package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶段打卡 - 管理端按用户/按计划查（阶段汇总）
 */
@Data
public class CheckInAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planId;
    private String userId;
    private String userNickName;
    private String userAccount;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    private Integer planDays;
    private Integer doneDays;
    private Integer insufficientDays;
    private Integer notDoneDays;
    /** 完成率 0-100（doneDays / 计划总天数） */
    private Integer completionRate;
}