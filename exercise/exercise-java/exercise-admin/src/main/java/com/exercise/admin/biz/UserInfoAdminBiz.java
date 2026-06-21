package com.exercise.admin.biz;

import com.exercise.campus.converter.UserInfoConverter;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.query.UserInfoQuery;
import com.exercise.campus.entity.vo.UserInfoVO;
import com.exercise.campus.service.UserInfoService;
import com.exercise.campus.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理端 Biz：编排 Service 返回 VO、权限校验入口
 */
@Component
public class UserInfoAdminBiz {

    @Autowired
    private UserInfoService userInfoService;

    public PageResultVO<UserInfoVO> loadDataList(UserInfoQuery query) {
        PageResultVO<UserInfo> page = userInfoService.pageList(query);
        List<UserInfoVO> list = page.getList().stream()
                .map(UserInfoConverter::toVO)
                .collect(Collectors.toList());
        return new PageResultVO<>(page.getTotal(), page.getPageNum(), page.getPageSize(), list);
    }

    public UserInfoVO detail(String userId) {
        return UserInfoConverter.toVO(userInfoService.getById(userId));
    }

    public void updateStatus(String userId, Integer status) {
        userInfoService.updateStatus(userId, status);
    }
}