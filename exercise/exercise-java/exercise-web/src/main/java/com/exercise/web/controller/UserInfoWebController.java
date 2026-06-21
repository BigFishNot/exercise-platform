package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.UserChangePasswordDTO;
import com.exercise.campus.entity.dto.UserLoginDTO;
import com.exercise.campus.entity.dto.UserRegisterDTO;
import com.exercise.campus.entity.dto.UserUpdateProfileDTO;
import com.exercise.campus.entity.vo.UserInfoVO;
import com.exercise.campus.entity.vo.UserLoginVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.UserInfoWebBiz;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 Controller
 * 路径形式：/api/userInfo/<动作>
 */
@RestController
@RequestMapping("/userInfo")
@Validated
public class UserInfoWebController extends BaseController {

    @Autowired
    private UserInfoWebBiz userInfoWebBiz;

    /** 注册：仅用户端开放，注册成功自动以 USER 角色登录 */
    @PostMapping("/register")
    public ResponseVO<UserLoginVO> register(@RequestBody @Valid UserRegisterDTO dto) {
        return success(userInfoWebBiz.register(dto));
    }

    /** 登录 */
    @PostMapping("/login")
    public ResponseVO<UserLoginVO> login(@RequestBody @Valid UserLoginDTO dto) {
        return success(userInfoWebBiz.login(dto));
    }

    /** 获取个人资料 */
    @GetMapping("/getProfile")
    public ResponseVO<UserInfoVO> getProfile() {
        return success(userInfoWebBiz.getProfile());
    }

    /** 更新个人资料 */
    @PostMapping("/updateProfile")
    public ResponseVO<Void> updateProfile(@RequestBody @Valid UserUpdateProfileDTO dto) {
        userInfoWebBiz.updateProfile(dto);
        return success();
    }

    /** 修改密码 */
    @PostMapping("/changePassword")
    public ResponseVO<Void> changePassword(@RequestBody @Valid UserChangePasswordDTO dto) {
        userInfoWebBiz.changePassword(dto);
        return success();
    }

    /** 登出：清理 Redis 中的 token */
    @PostMapping("/logout")
    public ResponseVO<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("studentToken");
        userInfoWebBiz.logoutWithToken(token);
        return success();
    }
}