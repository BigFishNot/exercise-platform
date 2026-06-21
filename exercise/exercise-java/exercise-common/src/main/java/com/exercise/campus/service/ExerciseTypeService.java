package com.exercise.campus.service;

import com.exercise.campus.entity.dto.ExerciseTypeAddDTO;
import com.exercise.campus.entity.dto.ExerciseTypeUpdateDTO;
import com.exercise.campus.entity.po.ExerciseType;
import com.exercise.campus.entity.query.ExerciseTypeQuery;
import com.exercise.campus.entity.vo.ExerciseTypeAdminVO;
import com.exercise.campus.entity.vo.ExerciseTypeVO;
import com.exercise.campus.vo.PageResultVO;

import java.util.List;

/**
 * 运动类型 Service
 */
public interface ExerciseTypeService {

    /**
     * 新增
     */
    String add(ExerciseTypeAddDTO dto);

    /**
     * 编辑
     */
    void update(ExerciseTypeUpdateDTO dto);

    /**
     * 启停
     */
    void updateStatus(String typeId, Integer status);

    /**
     * 删除（被引用时拒绝）
     */
    void delete(String typeId);

    /**
     * 单条详情
     */
    ExerciseType getById(String typeId);

    /**
     * 用户端下拉：仅 status=1
     */
    List<ExerciseTypeVO> listEnabled();

    /**
     * 管理端分页（含引用次数）
     */
    PageResultVO<ExerciseTypeAdminVO> pageList(ExerciseTypeQuery query);
}