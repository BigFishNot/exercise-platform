package com.exercise.campus.entity.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfoQuery extends PageQuery {

    /** 昵称模糊匹配 */
    private String nickNameFuzzy;

    /** 账号精确匹配 */
    private String account;

    /** 角色 */
    private Integer roleType;

    /** 状态 */
    private Integer status;
}