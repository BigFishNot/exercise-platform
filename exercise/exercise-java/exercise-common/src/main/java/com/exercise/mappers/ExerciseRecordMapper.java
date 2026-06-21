package com.exercise.mappers;

import com.exercise.campus.entity.po.ExerciseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ExerciseRecordMapper {

    int insert(ExerciseRecord po);

    int updateFinish(@Param("recordId") String recordId,
                     @Param("actualSeconds") Integer actualSeconds,
                     @Param("calories") java.math.BigDecimal calories,
                     @Param("status") Integer status,
                     @Param("endTime") Date endTime);

    int updateAbandon(@Param("recordId") String recordId,
                      @Param("abandonReason") String abandonReason,
                      @Param("status") Integer status,
                      @Param("endTime") Date endTime);

    ExerciseRecord selectById(@Param("recordId") String recordId);

    /** 当前用户进行中的运动记录（最多 1 条） */
    ExerciseRecord selectActiveByUserId(@Param("userId") String userId);

    /** 当日已完成/已放弃记录（按开始时间倒序） */
    List<ExerciseRecord> selectTodayByUserId(@Param("userId") String userId,
                                             @Param("exerciseDate") Date exerciseDate);

    /** 当日累计有效时长（仅 DONE） */
    Integer sumTodayActualSeconds(@Param("userId") String userId,
                                  @Param("exerciseDate") Date exerciseDate);

    /** 指定日期范围内每天的累计有效时长（仅 DONE），用于阶段日历聚合 */
    List<java.util.Map<String, Object>> sumActualSecondsByDateRange(
            @Param("userId") String userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}