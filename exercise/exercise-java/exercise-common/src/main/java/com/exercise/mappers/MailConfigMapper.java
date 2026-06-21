package com.exercise.mappers;

import com.exercise.campus.entity.po.MailConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MailConfigMapper {

    int upsert(MailConfig po);

    MailConfig select();
}