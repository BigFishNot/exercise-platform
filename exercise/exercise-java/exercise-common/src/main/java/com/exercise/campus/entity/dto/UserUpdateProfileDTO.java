package com.exercise.campus.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户更新个人资料入参
 */
@Data
public class UserUpdateProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickName;
    private String avatar;
    private Integer gender;
    private String email;

    @Past(message = "出生年月必须早于当前日期")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date birthDate;

    /** 身高(cm) */
    private BigDecimal height;

    /** 体重(kg) */
    private BigDecimal weight;
}