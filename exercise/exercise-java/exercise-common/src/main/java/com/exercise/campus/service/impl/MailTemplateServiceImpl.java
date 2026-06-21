package com.exercise.campus.service.impl;

import com.exercise.campus.converter.MailConverter;
import com.exercise.campus.entity.dto.MailTemplateUpsertDTO;
import com.exercise.campus.entity.po.MailTemplate;
import com.exercise.campus.entity.vo.MailTemplateVO;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.MailTemplateService;
import com.exercise.mappers.MailTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailTemplateServiceImpl implements MailTemplateService {

    @Autowired
    private MailTemplateMapper mailTemplateMapper;

    @Override
    public List<MailTemplateVO> list() {
        return mailTemplateMapper.selectList().stream()
                .map(MailConverter::toTemplateVO).collect(Collectors.toList());
    }

    @Override
    public MailTemplateVO getById(String templateId) {
        return MailConverter.toTemplateVO(mailTemplateMapper.selectById(templateId));
    }

    @Override
    public MailTemplateVO upsert(MailTemplateUpsertDTO dto) {
        if (dto.getTemplateType() == null
                || dto.getName() == null || dto.getName().isEmpty()
                || dto.getTitle() == null || dto.getTitle().isEmpty()
                || dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "模板字段不能为空");
        }
        MailTemplate po = MailConverter.toTemplatePO(dto);
        if (StringUtils.hasText(dto.getTemplateId())) {
            MailTemplate existing = mailTemplateMapper.selectById(dto.getTemplateId());
            if (existing == null) throw new BusinessException(ResponseCodeEnum.MAIL_TEMPLATE_NOT_FOUND);
            mailTemplateMapper.updateById(po);
        } else {
            // 新增时，分类已存在则提示
            MailTemplate byType = mailTemplateMapper.selectByType(dto.getTemplateType());
            if (byType != null) {
                throw new BusinessException(ResponseCodeEnum.MAIL_TEMPLATE_TYPE_DUPLICATE);
            }
            mailTemplateMapper.insert(po);
        }
        return MailConverter.toTemplateVO(mailTemplateMapper.selectById(po.getTemplateId()));
    }

    @Override
    public void delete(String templateId) {
        mailTemplateMapper.deleteById(templateId);
    }

    @Override
    public MailTemplate loadByType(Integer templateType) {
        return mailTemplateMapper.selectByType(templateType);
    }

    @Override
    public MailTemplate loadById(String templateId) {
        return mailTemplateMapper.selectById(templateId);
    }
}