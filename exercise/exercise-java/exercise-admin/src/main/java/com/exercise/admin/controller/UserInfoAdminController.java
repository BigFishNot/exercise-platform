package com.exercise.admin.controller;

import com.exercise.admin.biz.UserInfoAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.query.UserInfoQuery;
import com.exercise.campus.entity.vo.UserInfoVO;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.campus.vo.ResponseVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理 Controller（管理端）
 * 路径形式：/api/userInfo/<动作>，模块名小驼峰
 */
@RestController
@RequestMapping("/userInfo")
@Validated
public class UserInfoAdminController extends BaseController {

    @Autowired
    private UserInfoAdminBiz userInfoAdminBiz;

    /** 列表（搜索区 + 表格 + 分页） */
    @GetMapping("/loadDataList")
    public ResponseVO<PageResultVO<UserInfoVO>> loadDataList(UserInfoQuery query) {
        return success(userInfoAdminBiz.loadDataList(query));
    }

    @GetMapping("/detail")
    public ResponseVO<UserInfoVO> detail(@RequestParam @NotBlank String userId) {
        return success(userInfoAdminBiz.detail(userId));
    }

    /** 启停：status = 1 启用 / 0 停用 */
    @PostMapping("/updateStatus")
    public ResponseVO<Void> updateStatus(@RequestParam @NotBlank String userId,
                                         @RequestParam @NotNull Integer status) {
        userInfoAdminBiz.updateStatus(userId, status);
        return success();
    }
}