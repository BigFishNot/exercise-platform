package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.component.LoginRedisComponent;
import com.exercise.campus.converter.UserInfoConverter;
import com.exercise.campus.entity.dto.UserChangePasswordDTO;
import com.exercise.campus.entity.dto.UserLoginDTO;
import com.exercise.campus.entity.dto.UserRegisterDTO;
import com.exercise.campus.entity.dto.UserUpdateProfileDTO;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.vo.UserInfoVO;
import com.exercise.campus.entity.vo.UserLoginVO;
import com.exercise.campus.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户端 Biz：注册 / 登录 / 个人资料 / 改密 / 登出
 * 注册成功自动以 USER 角色登录并写登录态
 */
@Component
public class UserInfoWebBiz {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LoginRedisComponent loginRedisComponent;

    public UserLoginVO register(UserRegisterDTO dto) {
        String userId = userInfoService.register(
                dto.getAccount(),
                dto.getEmail(),
                dto.getNickName(),
                dto.getPassword(),
                dto.getGender(),
                dto.getBirthDate());
        UserInfo po = userInfoService.getById(userId);
        return buildLoginVO(po);
    }

    public UserLoginVO login(UserLoginDTO dto) {
        UserInfo po = userInfoService.login(dto.getAccount(), dto.getPassword());
        return buildLoginVO(po);
    }

    public UserInfoVO getProfile() {
        String userId = LoginContextHolder.requireUserId();
        return UserInfoConverter.toVO(userInfoService.getById(userId));
    }

    public void updateProfile(UserUpdateProfileDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        userInfoService.updateProfile(userId, dto.getNickName(), dto.getAvatar(),
                dto.getGender(), dto.getBirthDate(), dto.getHeight(), dto.getWeight(),
                dto.getEmail());
    }

    public void changePassword(UserChangePasswordDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        userInfoService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword());
    }

    public void logout() {
        // token 由前端从 header 传入；后端不持有明文 token，这里通过 userId 失效做不到（多端登录）
        // 简化：调用方从 controller 自行清理 redis
        LoginContextHolder.clear();
    }

    public void logoutWithToken(String token) {
        loginRedisComponent.cleanUser(token);
    }

    private UserLoginVO buildLoginVO(UserInfo po) {
        UserLoginVO vo = new UserLoginVO();
        String token = loginRedisComponent.createUserToken(
                po.getUserId(), po.getRoleType(), po.getAccount(), po.getNickName());
        vo.setToken(token);
        vo.setUserInfo(UserInfoConverter.toVO(po));
        return vo;
    }
}