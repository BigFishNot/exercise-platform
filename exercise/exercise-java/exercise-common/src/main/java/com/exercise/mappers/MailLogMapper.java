package com.exercise.mappers;

import com.exercise.campus.entity.po.MailLog;
import com.exercise.campus.entity.query.MailLogQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MailLogMapper {

    int insert(MailLog po);

    int countByQuery(MailLogQuery query);

    List<MailLog> selectList(MailLogQuery query);

    /** 当日 (userId, templateType) 是否已发过成功 */
    Integer countTodaySuccess(@Param("userId") String userId,
                              @Param("templateType") Integer templateType,
                              @Param("sendDate") Date sendDate);

    /** 区间内成功条数（管理员推送时去重用） */
    int countByUserTypeDate(@Param("userId") String userId,
                            @Param("templateType") Integer templateType,
                            @Param("sendDate") Date sendDate,
                            @Param("status") Integer status);
}