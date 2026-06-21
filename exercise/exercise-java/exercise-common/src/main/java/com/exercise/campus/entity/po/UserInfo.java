package com.exercise.campus.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户 PO
 * 表：user_info
 */
@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID（UUID） */
    private String userId;

    /** 账号（手机号 / 邮箱） */
    private String account;

    /** 邮箱 */
    private String email;

    /** 昵称 */
    private String nickName;

    /** 密码（BCrypt） */
    private String password;

    /** 头像 */
    private String avatar;

    /** 性别：0未知 1男 2女 */
    private Integer gender;

    /** 出生年月 */
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date birthDate;

    /** 身高(cm) */
    private BigDecimal height;

    /** 体重(kg) */
    private BigDecimal weight;

    /** 角色：1管理员 2普通用户 */
    private Integer roleType;

    /** 状态：0停用 1正常 */
    private Integer status;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerTime;

    /** 最近登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}