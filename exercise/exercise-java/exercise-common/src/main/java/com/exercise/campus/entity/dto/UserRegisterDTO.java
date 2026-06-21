package com.exercise.campus.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户注册入参（仅用户端）
 */
@Data
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 账号（手机号 / 邮箱） */
    @NotBlank(message = "账号不能为空")
    private String account;

    /** 邮箱（可选） */
    private String email;

    @NotBlank(message = "昵称不能为空")
    private String nickName;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 性别：0未知 1男 2女 */
    @NotNull(message = "性别不能为空")
    private Integer gender;

    /** 出生年月 */
    @NotNull(message = "出生年月不能为空")
    @Past(message = "出生年月必须早于当前日期")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date birthDate;
}