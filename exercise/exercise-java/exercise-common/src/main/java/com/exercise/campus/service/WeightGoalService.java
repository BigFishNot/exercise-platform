package com.exercise.campus.service;

import com.exercise.campus.entity.dto.WeightGoalAddDTO;
import com.exercise.campus.entity.dto.WeightGoalIdDTO;
import com.exercise.campus.entity.po.WeightGoal;
import com.exercise.campus.entity.query.WeightGoalQuery;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.vo.PageResultVO;

import java.util.List;

/**
 * 减肥目标 Service
 */
public interface WeightGoalService {

    /** 新增（自动归档已有 ACTIVE） */
    String add(String userId, WeightGoalAddDTO dto);

    /** 修改（仅生效中） */
    void update(String userId, WeightGoalAddDTO dto);

    /** 归档（仅生效中） */
    void archive(String userId, WeightGoalIdDTO dto);

    /** 当前生效中目标（无则 null） */
    WeightGoalVO getActive(String userId);

    /** 单条详情 */
    WeightGoalVO getById(String goalId);

    /** 用户全部目标 */
    List<WeightGoalVO> listByUser(String userId);

    /** 管理端分页 */
    PageResultVO<WeightGoalVO> pageList(WeightGoalQuery query);

    /** 内部用：拉 PO（无记录返回 null） */
    WeightGoal loadActive(String userId);
}