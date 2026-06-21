package com.exercise.campus.service.impl;

import com.exercise.campus.component.EncryptUtil;
import com.exercise.campus.converter.MailConverter;
import com.exercise.campus.entity.dto.MailConfigUpdateDTO;
import com.exercise.campus.entity.po.MailConfig;
import com.exercise.campus.entity.vo.MailConfigVO;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.MailConfigService;
import com.exercise.campus.utils.MailTimeUtils;
import com.exercise.mappers.MailConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class MailConfigServiceImpl implements MailConfigService {

    @Autowired
    private MailConfigMapper mailConfigMapper;

    @Override
    public MailConfigVO get() {
        return MailConverter.toConfigVO(mailConfigMapper.select());
    }

    @Override
    public void update(MailConfigUpdateDTO dto) {
        // 简单格式校验
        if (dto.getSmtpHost() == null || dto.getSmtpHost().isEmpty()) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "SMTP 主机不能为空");
        }
        if (dto.getSmtpPort() == null || dto.getSmtpPort() <= 0) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "SMTP 端口非法");
        }
        if (dto.getSmtpUsername() == null || dto.getSmtpUsername().isEmpty()) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "SMTP 账号不能为空");
        }
        if (!MailTimeUtils.isValidTimePoints(dto.getSendTimePoints())) {
            throw new BusinessException(ResponseCodeEnum.MAIL_TIME_POINTS_INVALID);
        }

        MailConfig existing = mailConfigMapper.select();
        String encryptedPwd;
        if (dto.getSmtpPassword() != null && !dto.getSmtpPassword().isEmpty()) {
            encryptedPwd = EncryptUtil.encrypt(dto.getSmtpPassword());
        } else if (existing != null) {
            encryptedPwd = existing.getSmtpPassword();
        } else {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "请输入 SMTP 授权码");
        }
        MailConfig po = MailConverter.toPO(dto, encryptedPwd);
        mailConfigMapper.upsert(po);
    }

    @Override
    public MailConfig loadPO() {
        return mailConfigMapper.select();
    }
}