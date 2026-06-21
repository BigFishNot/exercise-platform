package com.exercise.mappers;

import com.exercise.campus.entity.po.ExerciseType;
import com.exercise.campus.entity.query.ExerciseTypeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 运动类型 Mapper
 */
@Mapper
public interface ExerciseTypeMapper {

    int insert(ExerciseType po);

    int updateById(ExerciseType po);

    int updateStatus(@Param("typeId") String typeId, @Param("status") Integer status);

    int deleteById(@Param("typeId") String typeId);

    ExerciseType selectById(@Param("typeId") String typeId);

    Integer countByName(@Param("name") String name);

    /** 引用次数：当前先固定返回 0，等 exerciseRecord 模块接入时再联表实现 */
    Integer countRef(@Param("typeId") String typeId);

    int countByQuery(ExerciseTypeQuery query);

    List<ExerciseType> selectList(ExerciseTypeQuery query);

    /** 用户端下拉：仅查 status=1，按 sort 升序 */
    List<ExerciseType> selectEnabled();
}