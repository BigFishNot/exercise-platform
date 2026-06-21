package com.exercise.mappers;

import com.exercise.campus.entity.po.WeightGoal;
import com.exercise.campus.entity.query.WeightGoalQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WeightGoalMapper {

    int insert(WeightGoal po);

    int updateById(WeightGoal po);

    /** 归档：同时维护 active_marker 实现"同用户唯一 ACTIVE" */
    int updateArchive(@Param("goalId") String goalId,
                      @Param("activeMarker") String activeMarker,
                      @Param("archiveTime") java.util.Date archiveTime);

    WeightGoal selectById(@Param("goalId") String goalId);

    /** 当前用户生效中目标（最多 1 条） */
    WeightGoal selectActiveByUserId(@Param("userId") String userId);

    int countByQuery(WeightGoalQuery query);

    List<WeightGoal> selectList(WeightGoalQuery query);
}