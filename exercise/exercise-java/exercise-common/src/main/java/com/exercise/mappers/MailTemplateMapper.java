package com.exercise.mappers;

import com.exercise.campus.entity.po.MailTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MailTemplateMapper {

    int insert(MailTemplate po);

    int updateById(MailTemplate po);

    int deleteById(@Param("templateId") String templateId);

    MailTemplate selectById(@Param("templateId") String templateId);

    MailTemplate selectByType(@Param("templateType") Integer templateType);

    List<MailTemplate> selectList();
}