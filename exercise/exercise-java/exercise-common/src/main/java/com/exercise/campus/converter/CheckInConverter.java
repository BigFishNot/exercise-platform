package com.exercise.campus.converter;

import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.vo.CheckInAdminVO;
import com.exercise.campus.entity.vo.CheckInTodayVO;
import com.exercise.campus.entity.vo.PlanCalendarDayVO;
import com.exercise.campus.enums.CheckInStatusEnum;
import com.exercise.campus.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 打卡判定 Converter（按需计算）
 */
public class CheckInConverter {

    /** 判定状态：actualMinutes vs targetMinutes */
    public static Integer judgeStatus(int actualMinutes, int targetMinutes) {
        if (actualMinutes >= targetMinutes) return CheckInStatusEnum.DONE.getStatus();
        if (actualMinutes > 0) return CheckInStatusEnum.INSUFFICIENT.getStatus();
        return CheckInStatusEnum.NOT_DONE.getStatus();
    }

    public static CheckInTodayVO toTodayVO(ExercisePlan plan, int actualMinutes) {
        CheckInTodayVO vo = new CheckInTodayVO();
        if (plan == null) {
            vo.setStatus(CheckInStatusEnum.NOT_DONE.getStatus());
            vo.setStatusName(CheckInStatusEnum.NOT_DONE.getDesc());
            return vo;
        }
        vo.setPlanId(plan.getPlanId());
        vo.setUserId(plan.getUserId());
        vo.setExerciseDate(todayGMT8());
        int target = plan.getDailyTargetMinutes() == null ? 0 : plan.getDailyTargetMinutes();
        vo.setTargetMinutes(target);
        vo.setActualMinutes(actualMinutes);
        int rate = target == 0 ? 0 : Math.min(100, (int) Math.round(actualMinutes * 100.0 / target));
        vo.setCompletionRate(rate);
        int status = judgeStatus(actualMinutes, target);
        vo.setStatus(status);
        vo.setStatusName(CheckInStatusEnum.of(status).getDesc());
        return vo;
    }

    public static PlanCalendarDayVO toDayVO(Date date, int actualMinutes, int targetMinutes, boolean isFuture) {
        PlanCalendarDayVO vo = new PlanCalendarDayVO();
        vo.setDate(date);
        vo.setTargetMinutes(targetMinutes);
        vo.setActualMinutes(actualMinutes);
        if (isFuture) {
            vo.setCheckInStatus(CheckInStatusEnum.FUTURE.name());
        } else {
            vo.setCheckInStatus(CheckInStatusEnum.of(judgeStatus(actualMinutes, targetMinutes)).name());
        }
        return vo;
    }

    public static CheckInAdminVO toAdminVO(ExercisePlan plan, int doneDays, int insufficientDays) {
        CheckInAdminVO vo = new CheckInAdminVO();
        if (plan == null) return vo;
        vo.setPlanId(plan.getPlanId());
        vo.setUserId(plan.getUserId());
        vo.setStartDate(plan.getStartDate());
        vo.setEndDate(plan.getEndDate());
        long total = DateUtils.daysBetween(plan.getStartDate(), plan.getEndDate()) + 1;
        vo.setPlanDays((int) total);
        vo.setDoneDays(doneDays);
        vo.setInsufficientDays(insufficientDays);
        vo.setNotDoneDays((int) Math.max(0, total - doneDays - insufficientDays));
        vo.setCompletionRate(total == 0 ? 0 : (int) Math.round(doneDays * 100.0 / total));
        return vo;
    }

    private static Date todayGMT8() {
        Calendar c = Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT+8"));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}