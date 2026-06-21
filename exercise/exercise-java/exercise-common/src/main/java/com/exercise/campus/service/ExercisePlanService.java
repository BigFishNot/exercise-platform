package com.exercise.campus.service;

import com.exercise.campus.entity.dto.PlanAddDTO;
import com.exercise.campus.entity.dto.PlanUpdateDailyTargetDTO;
import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.query.ExercisePlanQuery;
import com.exercise.campus.entity.vo.PlanCalendarVO;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.vo.PageResultVO;

import java.util.List;

/**
 * 阶段计划 Service
 */
public interface ExercisePlanService {

    /** 创建计划（用户端） */
    String add(String userId, PlanAddDTO dto);

    /** 修改每日目标 + 备注（仅进行中） */
    void updateDailyTarget(String userId, PlanUpdateDailyTargetDTO dto);

    /** 取消计划（仅进行中） */
    void cancel(String userId, String planId);

    /** 当前用户当前进行中计划（无则返回 null） */
    PlanVO getCurrent(String userId);

    /** 当前用户当前进行中计划（内部用，不抛错；返回 null 时不抛 RECORD_NOT_EXISTS） */
    ExercisePlan loadOngoing(String userId);

    /** 当前用户所有计划（按开始日期倒序，简化版） */
    List<PlanVO> listByUser(String userId);

    /** 阶段日历 */
    PlanCalendarVO getCalendar(String userId, String planId);

    /** 单条详情（鉴权：仅本人或管理员） */
    PlanVO detail(String planId);

    /** 管理端按用户分页 */
    PageResultVO<PlanVO> pageList(ExercisePlanQuery query);

    /** 内部使用：按 planId 加载 PO（不抛错，返回 null） */
    ExercisePlan loadById(String planId);
}