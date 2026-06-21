package com.exercise.campus.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码入参
 */
@Data
public class UserChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}