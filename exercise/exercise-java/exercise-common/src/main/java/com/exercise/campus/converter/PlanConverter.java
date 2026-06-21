package com.exercise.campus.converter;

import com.exercise.campus.entity.dto.PlanAddDTO;
import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.vo.PlanCalendarDayVO;
import com.exercise.campus.entity.vo.PlanCalendarVO;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.enums.PlanStatusEnum;

import java.util.UUID;

/**
 * 阶段计划 Converter
 */
public class PlanConverter {

    public static ExercisePlan toPO(PlanAddDTO dto, String userId) {
        ExercisePlan po = new ExercisePlan();
        po.setPlanId(UUID.randomUUID().toString().replace("-", ""));
        po.setUserId(userId);
        po.setStartDate(dto.getStartDate());
        po.setEndDate(dto.getEndDate());
        po.setDailyTargetMinutes(dto.getDailyTargetMinutes());
        po.setRemark(dto.getRemark());
        po.setStatus(PlanStatusEnum.ONGOING.getStatus());
        po.setOngoingUnique(userId); // ONGOING 时与 userId 相同
        return po;
    }

    public static PlanVO toVO(ExercisePlan po) {
        if (po == null) return null;
        PlanVO vo = new PlanVO();
        vo.setPlanId(po.getPlanId());
        vo.setUserId(po.getUserId());
        vo.setStartDate(po.getStartDate());
        vo.setEndDate(po.getEndDate());
        vo.setDailyTargetMinutes(po.getDailyTargetMinutes());
        vo.setRemark(po.getRemark());
        vo.setStatus(po.getStatus());
        vo.setStatusName(po.getStatus() != null
                ? PlanStatusEnum.of(po.getStatus()).getDesc()
                : null);
        vo.setFinishTime(po.getFinishTime());
        vo.setCreateTime(po.getCreateTime());

        // 阶段天数计算
        if (po.getStartDate() != null && po.getEndDate() != null) {
            long days = daysBetween(po.getStartDate(), po.getEndDate());
            vo.setDaysTotal((int) (days + 1));
            long elapsed = daysBetween(po.getStartDate(), new java.util.Date());
            if (elapsed < 0) vo.setDaysElapsed(0);
            else if (elapsed >= days) vo.setDaysElapsed(vo.getDaysTotal());
            else vo.setDaysElapsed((int) (elapsed + 1));
            vo.setDaysRemaining(vo.getDaysTotal() - vo.getDaysElapsed());
        }

        // 当日累计时长 / 当日是否完成（exerciseCheckIn 模块接入后回填）
        vo.setTodayActualMinutes(0);
        vo.setTodayDone(false);

        return vo;
    }

    public static PlanCalendarVO toCalendarVO(ExercisePlan po, java.util.List<PlanCalendarDayVO> days) {
        if (po == null) return null;
        PlanCalendarVO vo = new PlanCalendarVO();
        vo.setPlanId(po.getPlanId());
        vo.setStartDate(po.getStartDate());
        vo.setEndDate(po.getEndDate());
        vo.setDailyTargetMinutes(po.getDailyTargetMinutes());
        vo.setDays(days == null ? java.util.Collections.emptyList() : days);
        return vo;
    }

    /** 含首尾的天数差（同日返回 0） */
    public static long daysBetween(java.util.Date start, java.util.Date end) {
        if (start == null || end == null) return 0;
        long ms = end.getTime() - start.getTime();
        if (ms < 0) return -1;
        return ms / (1000L * 60 * 60 * 24);
    }
}