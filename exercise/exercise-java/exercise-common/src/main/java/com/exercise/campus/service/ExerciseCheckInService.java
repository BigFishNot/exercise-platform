package com.exercise.campus.service;

import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.query.CheckInAdminQuery;
import com.exercise.campus.entity.vo.CheckInAdminVO;
import com.exercise.campus.entity.vo.CheckInTodayVO;
import com.exercise.campus.entity.vo.PlanCalendarDayVO;
import com.exercise.campus.vo.PageResultVO;

import java.util.Date;
import java.util.List;

/**
 * 打卡判定 Service
 * 按需计算：直接聚合 exercise_record 表，不持久化 check_in 缓存
 */
public interface ExerciseCheckInService {

    /** 用户端：当日打卡状态（无进行中计划返回 NOT_DONE 默认值） */
    CheckInTodayVO getToday(String userId);

    /** 用户端：阶段日历（每天 DONE / INSUFFICIENT / NOT_DONE / FUTURE） */
    List<PlanCalendarDayVO> getPlanCalendar(ExercisePlan plan);

    /** 内部用：完成一条运动后重算当日（目前无持久化，预留接口供后续定时任务复用） */
    void onRecordFinished(String userId, Date exerciseDate);

    /** 管理端：分页查询阶段汇总（doneDays / insufficientDays / notDoneDays） */
    PageResultVO<CheckInAdminVO> pageList(CheckInAdminQuery query);
}