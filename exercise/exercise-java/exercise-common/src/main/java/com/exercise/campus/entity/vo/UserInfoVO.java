package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户 VO（前后端通用，不含敏感字段）
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String account;
    private String email;
    private String nickName;
    private String avatar;
    private Integer gender;
    private String genderName;

    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date birthDate;

    private BigDecimal height;
    private BigDecimal weight;
    private Integer roleType;
    private String roleTypeName;
    private Integer status;
    private String statusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    /** BMI（kg/m^2，后端算） */
    private BigDecimal bmi;
}