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

    /* === 统计聚合 === */

    /** 用户全局运动统计（返回 Map: totalRecords/totalActiveSeconds/totalCalories/activeDays 等） */
    java.util.Map<String, Object> aggregateUserStats(@Param("userId") String userId);

    /** 用户区间内（按 startDate/endDate）每天状态计数，返回 [doneDays, insufficientDays] */
    java.util.Map<String, Object> countCheckInByDateRange(@Param("userId") String userId,
                                                          @Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);

    /** 用户近 N 天有效运动天数（按日期去重，仅 DONE） */
    Integer countActiveDaysSince(@Param("userId") String userId,
                                  @Param("sinceDate") Date sinceDate);

    /** 平台总览：今日 / 近 7 天 / 近 30 天 记录数与 DONE 数 */
    java.util.Map<String, Object> aggregatePlatformRecords(@Param("sinceDate") Date sinceDate);

    /** 平台总览：近 7 天有 DONE 记录的去重用户数 */
    Long countActiveUsersSince(@Param("sinceDate") Date sinceDate);

    /** 平台总览：热门运动 Top N（按 DONE 记录数倒序） */
    List<java.util.Map<String, Object>> topExerciseTypes(@Param("limit") Integer limit);
}