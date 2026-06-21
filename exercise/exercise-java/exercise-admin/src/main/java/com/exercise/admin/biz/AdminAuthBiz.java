package com.exercise.admin.biz;

import com.exercise.campus.component.LoginRedisComponent;
import com.exercise.campus.converter.UserInfoConverter;
import com.exercise.campus.entity.dto.UserLoginDTO;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.vo.UserInfoVO;
import com.exercise.campus.entity.vo.UserLoginVO;
import com.exercise.campus.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 管理端 Auth Biz：管理员登录 / 登出
 * 复用 common 的 UserInfoService，仅校验 roleType=ADMIN
 */
@Component
public class AdminAuthBiz {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LoginRedisComponent loginRedisComponent;

    public UserLoginVO login(UserLoginDTO dto) {
        UserInfo po = userInfoService.login(dto.getAccount(), dto.getPassword());
        UserInfoVO vo = UserInfoConverter.toVO(po);
        String token = loginRedisComponent.createAdminToken(
                po.getUserId(), po.getRoleType(), po.getAccount(), po.getNickName());
        UserLoginVO result = new UserLoginVO();
        result.setToken(token);
        result.setUserInfo(vo);
        return result;
    }

    public void logout(String token) {
        loginRedisComponent.cleanAdmin(token);
    }
}