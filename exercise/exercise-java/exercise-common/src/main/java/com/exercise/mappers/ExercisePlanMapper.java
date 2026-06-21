package com.exercise.mappers;

import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.query.ExercisePlanQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 阶段计划 Mapper
 */
@Mapper
public interface ExercisePlanMapper {

    int insert(ExercisePlan po);

    int updateById(ExercisePlan po);

    /** 状态机切换：同时维护 ongoing_unique 实现"同用户唯一进行中" */
    int updateStatus(@Param("planId") String planId,
                     @Param("status") Integer status,
                     @Param("ongoingUnique") String ongoingUnique,
                     @Param("finishTime") java.util.Date finishTime);

    ExercisePlan selectById(@Param("planId") String planId);

    /** 取用户当前进行中计划（最多一条） */
    ExercisePlan selectOngoingByUserId(@Param("userId") String userId);

    int countByQuery(ExercisePlanQuery query);

    List<ExercisePlan> selectList(ExercisePlanQuery query);
}