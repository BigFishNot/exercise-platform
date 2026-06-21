package com.exercise.campus.service;

import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.query.UserInfoQuery;
import com.exercise.campus.vo.PageResultVO;

import java.util.List;

/**
 * 用户 Service 接口（单领域业务逻辑）
 */
public interface UserInfoService {

    String register(String account, String email, String nickName, String rawPassword,
                    Integer gender, java.util.Date birthDate);

    UserInfo login(String account, String rawPassword);

    UserInfo getById(String userId);

    boolean updateProfile(String userId, String nickName, String avatar, Integer gender,
                          java.util.Date birthDate, java.math.BigDecimal height,
                          java.math.BigDecimal weight, String email);

    void changePassword(String userId, String oldPwd, String newPwd);

    void resetPassword(String userId, String newRawPwd);

    void updateStatus(String userId, Integer status);

    PageResultVO<UserInfo> pageList(UserInfoQuery query);

    List<UserInfo> listEnabled();
}