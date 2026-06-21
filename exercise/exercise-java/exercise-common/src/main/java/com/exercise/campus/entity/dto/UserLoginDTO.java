package com.exercise.campus.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录入参
 */
@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;
}