package com.exercise.campus.service.impl;

import com.exercise.campus.component.PasswordUtil;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.query.UserInfoQuery;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.enums.RoleTypeEnum;
import com.exercise.campus.enums.StatusEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.UserInfoService;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.mappers.UserInfoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 用户 Service 实现
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(String account, String email, String nickName, String rawPassword,
                           Integer gender, Date birthDate) {
        // 账号格式校验：手机号或邮箱
        if (!StringUtils.hasText(account)) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "账号不能为空");
        }
        if (!PasswordUtil.isValidFormat(rawPassword)) {
            throw new BusinessException(ResponseCodeEnum.PASSWORD_WEAK);
        }
        if (nickName == null || nickName.length() < 2 || nickName.length() > 20) {
            throw new BusinessException(ResponseCodeEnum.NICKNAME_INVALID);
        }
        if (userInfoMapper.countByAccount(account) > 0) {
            throw new BusinessException(ResponseCodeEnum.USER_ALREADY_EXISTS);
        }
        if (StringUtils.hasText(email) && userInfoMapper.countByEmail(email) > 0) {
            throw new BusinessException(ResponseCodeEnum.USER_ALREADY_EXISTS);
        }

        UserInfo po = new UserInfo();
        po.setUserId(UUID.randomUUID().toString().replace("-", ""));
        po.setAccount(account);
        po.setEmail(email);
        po.setNickName(nickName);
        po.setPassword(PasswordUtil.encrypt(rawPassword));
        po.setGender(gender == null ? 0 : gender);
        po.setBirthDate(birthDate);
        po.setRoleType(RoleTypeEnum.USER.getRoleType());
        po.setStatus(StatusEnum.ENABLED.getStatus());
        po.setRegisterTime(new Date());
        po.setLastLoginTime(new Date());
        userInfoMapper.insert(po);
        log.info("[register] userId={} account={}", po.getUserId(), po.getAccount());
        return po.getUserId();
    }

    @Override
    public UserInfo login(String account, String rawPassword) {
        if (!StringUtils.hasText(account) || !StringUtils.hasText(rawPassword)) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "账号或密码不能为空");
        }
        UserInfo po = userInfoMapper.selectByAccount(account);
        if (po == null) {
            // 也尝试邮箱登录
            po = userInfoMapper.selectByEmail(account);
        }
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.PASSWORD_ERROR);
        }
        if (!StatusEnum.ENABLED.getStatus().equals(po.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.USER_DISABLED);
        }
        if (!PasswordUtil.matches(rawPassword, po.getPassword())) {
            throw new BusinessException(ResponseCodeEnum.PASSWORD_ERROR);
        }
        userInfoMapper.updateLastLoginTime(po.getUserId());
        po.setPassword(null);
        return po;
    }

    @Override
    public UserInfo getById(String userId) {
        UserInfo po = userInfoMapper.selectById(userId);
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXISTS);
        }
        po.setPassword(null);
        return po;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfile(String userId, String nickName, String avatar, Integer gender,
                                 Date birthDate, java.math.BigDecimal height,
                                 java.math.BigDecimal weight, String email) {
        if (StringUtils.hasText(nickName) && (nickName.length() < 2 || nickName.length() > 20)) {
            throw new BusinessException(ResponseCodeEnum.NICKNAME_INVALID);
        }
        if (height != null && (height.compareTo(java.math.BigDecimal.ZERO) < 0
                || height.compareTo(new java.math.BigDecimal("300")) > 0)) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "身高范围 0-300 cm");
        }
        if (weight != null && (weight.compareTo(java.math.BigDecimal.ZERO) < 0
                || weight.compareTo(new java.math.BigDecimal("500")) > 0)) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "体重范围 0-500 kg");
        }
        if (StringUtils.hasText(email)) {
            Integer cnt = userInfoMapper.countByEmail(email);
            if (cnt > 0) {
                UserInfo exist = userInfoMapper.selectByEmail(email);
                if (exist != null && !exist.getUserId().equals(userId)) {
                    throw new BusinessException(ResponseCodeEnum.USER_ALREADY_EXISTS);
                }
            }
        }
        UserInfo po = new UserInfo();
        po.setUserId(userId);
        po.setNickName(nickName);
        po.setAvatar(avatar);
        po.setGender(gender);
        po.setBirthDate(birthDate);
        po.setHeight(height);
        po.setWeight(weight);
        po.setEmail(email);
        return userInfoMapper.updateById(po) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String userId, String oldPwd, String newPwd) {
        if (!PasswordUtil.isValidFormat(newPwd)) {
            throw new BusinessException(ResponseCodeEnum.PASSWORD_WEAK);
        }
        UserInfo po = userInfoMapper.selectById(userId);
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXISTS);
        }
        if (!PasswordUtil.matches(oldPwd, po.getPassword())) {
            throw new BusinessException(ResponseCodeEnum.PASSWORD_ERROR, "原密码错误");
        }
        userInfoMapper.updatePassword(userId, PasswordUtil.encrypt(newPwd));
    }

    @Override
    public void resetPassword(String userId, String newRawPwd) {
        if (!PasswordUtil.isValidFormat(newRawPwd)) {
            throw new BusinessException(ResponseCodeEnum.PASSWORD_WEAK);
        }
        userInfoMapper.updatePassword(userId, PasswordUtil.encrypt(newRawPwd));
    }

    @Override
    public void updateStatus(String userId, Integer status) {
        UserInfo po = userInfoMapper.selectById(userId);
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXISTS);
        }
        userInfoMapper.updateStatus(userId, status);
    }

    @Override
    public PageResultVO<UserInfo> pageList(UserInfoQuery query) {
        Integer total = userInfoMapper.countByQuery(query);
        if (total == null || total == 0) {
            return PageResultVO.empty();
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<UserInfo> list = userInfoMapper.selectList(query);
        list.forEach(u -> u.setPassword(null));
        return PageResultVO.of(new PageInfo<>(list));
    }

    @Override
    public List<UserInfo> listEnabled() {
        UserInfoQuery q = new UserInfoQuery();
        q.setStatus(StatusEnum.ENABLED.getStatus());
        List<UserInfo> list = userInfoMapper.selectList(q);
        if (list == null) {
            return Collections.emptyList();
        }
        list.forEach(u -> u.setPassword(null));
        return list;
    }
}