package com.exercise.mappers;

import com.exercise.campus.entity.po.BodyData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BodyDataMapper {

    /** 同一日 upsert：若当天已有记录则更新，否则插入 */
    int upsert(BodyData po);

    int updateById(BodyData po);

    int deleteById(@Param("id") Long id);

    BodyData selectById(@Param("id") Long id);

    /** 当日最新一条 */
    BodyData selectToday(@Param("userId") String userId,
                         @Param("recordDate") Date recordDate);

    /** 区间内趋势（升序，按日期） */
    List<BodyData> selectTrendByRange(@Param("userId") String userId,
                                      @Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate);

    /** 最近 N 条（管理端） */
    List<BodyData> selectRecent(@Param("userId") String userId,
                                @Param("limit") Integer limit);
}