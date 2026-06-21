package com.exercise.campus.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功返回 VO：包含 token 与基础用户信息
 */
@Data
public class UserLoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private UserInfoVO userInfo;
}