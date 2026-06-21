package com.exercise.admin.controller;

import com.exercise.admin.biz.AdminAuthBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.UserLoginDTO;
import com.exercise.campus.entity.vo.UserLoginVO;
import com.exercise.campus.vo.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 Auth Controller
 * 路径：/api/admin/<动作>
 */
@RestController
@RequestMapping("/admin")
@Validated
public class AdminAuthController extends BaseController {

    @Autowired
    private AdminAuthBiz adminAuthBiz;

    @PostMapping("/login")
    public ResponseVO<UserLoginVO> login(@RequestBody @Valid UserLoginDTO dto) {
        return success(adminAuthBiz.login(dto));
    }

    @PostMapping("/logout")
    public ResponseVO<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("adminToken");
        adminAuthBiz.logout(token);
        return success();
    }
}