package com.exercise.campus.service.impl;

import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.po.WeightGoal;
import com.exercise.campus.entity.vo.CheckInTodayVO;
import com.exercise.campus.entity.vo.HotExerciseTypeVO;
import com.exercise.campus.entity.vo.PersonalSummaryVO;
import com.exercise.campus.entity.vo.PlatformOverviewVO;
import com.exercise.campus.service.BodyDataService;
import com.exercise.campus.service.ExerciseCheckInService;
import com.exercise.campus.service.ExercisePlanService;
import com.exercise.campus.service.StatisticsService;
import com.exercise.campus.service.WeightGoalService;
import com.exercise.campus.utils.DateUtils;
import com.exercise.mappers.ExercisePlanMapper;
import com.exercise.mappers.ExerciseRecordMapper;
import com.exercise.mappers.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计 Service 实现
 * 聚合数据：用户汇总（自身数据）+ 平台总览（全表聚合）
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired private ExerciseRecordMapper exerciseRecordMapper;
    @Autowired private ExercisePlanMapper exercisePlanMapper;
    @Autowired private UserInfoMapper userInfoMapper;

    @Autowired private ExercisePlanService exercisePlanService;
    @Autowired private WeightGoalService weightGoalService;
    @Autowired private BodyDataService bodyDataService;
    @Autowired private ExerciseCheckInService exerciseCheckInService;

    @Override
    public PersonalSummaryVO personalSummary(String userId) {
        PersonalSummaryVO vo = new PersonalSummaryVO();

        // 1. 阶段计划
        ExercisePlan plan = exercisePlanService.loadOngoing(userId);
        if (plan != null) {
            vo.setHasActivePlan(true);
            vo.setPlanId(plan.getPlanId());
            vo.setStartDate(plan.getStartDate());
            vo.setEndDate(plan.getEndDate());
            vo.setDailyTargetMinutes(plan.getDailyTargetMinutes());

            long total = daysBetween(plan.getStartDate(), plan.getEndDate()) + 1;
            vo.setTotalDays((int) total);

            // 阶段内 done/insufficient
            Map<String, Object> ck = exerciseRecordMapper.countCheckInByDateRange(
                    userId, plan.getStartDate(), plan.getEndDate());
            int done = toInt(ck == null ? 0 : ck.get("doneDays"));
            int ins  = toInt(ck == null ? 0 : ck.get("insufficientDays"));
            vo.setDoneDays(done);
            vo.setInsufficientDays(ins);
            int compPct = total == 0 ? 0 : (int) Math.round(done * 100.0 / total);
            vo.setPlanCompletionRate(compPct);

            int today = (int) daysBetween(plan.getStartDate(), DateUtils.todayGMT8());
            int elapsed = Math.max(0, Math.min((int) total, today + 1));
            vo.setDaysRemaining((int) total - elapsed);
        } else {
            vo.setHasActivePlan(false);
        }

        // 2. 运动累计
        Map<String, Object> ex = exerciseRecordMapper.aggregateUserStats(userId);
        if (ex != null) {
            vo.setTotalActiveDays(toInt(ex.get("activeDays")));
            vo.setTotalActiveSeconds(toLong(ex.get("totalActiveSeconds")));
            BigDecimal cal = ex.get("totalCalories") == null
                    ? BigDecimal.ZERO
                    : new BigDecimal(ex.get("totalCalories").toString()).setScale(2, RoundingMode.HALF_UP);
            vo.setTotalCalories(cal);
            if (vo.getTotalActiveDays() != null && vo.getTotalActiveDays() > 0
                    && vo.getTotalActiveSeconds() != null) {
                vo.setAvgSecondsPerActiveDay(
                        vo.getTotalActiveSeconds() / vo.getTotalActiveDays());
            }
        }
        // 7d / 30d
        Date since7  = daysBefore(7);
        Date since30 = daysBefore(30);
        vo.setLast7DaysActiveDays(exerciseRecordMapper.countActiveDaysSince(userId, since7));
        vo.setLast30DaysActiveDays(exerciseRecordMapper.countActiveDaysSince(userId, since30));

        // 3. 身体数据
        try {
            com.exercise.campus.entity.vo.BodyDataSummaryVO bs = bodyDataService
                    .getTrend(userId, new com.exercise.campus.entity.query.BodyDataTrendQuery() {{
                        setRange("30d");
                    }}).summary();
            if (bs != null) {
                vo.setLatestWeight(bs.getLatestWeight());
                vo.setLatestBmi(bs.getLatestBmi());
                vo.setWeightChange7d(bs.getWeightChange7d());
            }
        } catch (Exception ignored) {}
        WeightGoal goal = weightGoalService.loadActive(userId);
        if (goal != null) vo.setTargetWeight(goal.getTargetWeight());
        if (vo.getLatestWeight() != null && vo.getTargetWeight() != null) {
            vo.setDistanceToTarget(vo.getLatestWeight().subtract(vo.getTargetWeight())
                    .setScale(2, RoundingMode.HALF_UP));
        }

        // 4. 今日打卡
        try {
            CheckInTodayVO today = exerciseCheckInService.getToday(userId);
            if (today != null) {
                vo.setTodayCheckInStatus(today.getStatus());
                vo.setTodayCheckInStatusName(today.getStatusName());
                vo.setTodayActualMinutes(today.getActualMinutes());
                vo.setTodayTargetMinutes(today.getTargetMinutes());
                vo.setTodayCompletionRate(today.getCompletionRate());
            }
        } catch (Exception ignored) {}

        return vo;
    }

    @Override
    public PlatformOverviewVO platformOverview() {
        PlatformOverviewVO vo = new PlatformOverviewVO();

        Date since7  = daysBefore(7);
        Date since30 = daysBefore(30);

        // 用户
        Map<String, Object> userAgg = userInfoMapper.aggregatePlatformUsers(since30);
        if (userAgg != null) {
            vo.setTotalUsers(toLong(userAgg.get("totalUsers")));
            vo.setEnabledUsers(toLong(userAgg.get("enabledUsers")));
            vo.setNewUsersToday(toLong(userAgg.get("newToday")));
            vo.setNewUsers30d(toLong(userAgg.get("newSince")));
        }
        vo.setNewUsers7d(exerciseRecordMapper.countActiveUsersSince(since7));

        // 运动
        Map<String, Object> rec = exerciseRecordMapper.aggregatePlatformRecords(since30);
        if (rec != null) {
            vo.setTotalRecords(toLong(rec.get("totalRecords")));
            vo.setTodayRecordCount(toLong(rec.get("todayRecordCount")));
            vo.setTodayDoneCount(toLong(rec.get("todayDoneCount")));
            vo.setRecords30d(toLong(rec.get("recordsSince")));
            vo.setTotalActiveSeconds(toLong(rec.get("totalActiveSeconds")));
            Object cal = rec.get("totalCalories");
            if (cal != null) {
                vo.setTotalCalories(new BigDecimal(cal.toString()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        vo.setRecords7d(toLong(exerciseRecordMapper.aggregatePlatformRecords(since7).get("recordsSince")));

        // 阶段计划
        vo.setOngoingPlans(exercisePlanMapper.countOngoing());

        // 邮件（mail 模块未做时为 0，前端按 0 渲染即可）
        vo.setMailSent7d(0L);
        vo.setMailSentSuccess7d(0L);
        vo.setMailSuccessRate7d(0);

        // 热门运动 Top 5
        List<Map<String, Object>> tops = exerciseRecordMapper.topExerciseTypes(5);
        if (tops != null) {
            List<HotExerciseTypeVO> hotList = new ArrayList<>();
            for (Map<String, Object> m : tops) {
                HotExerciseTypeVO h = new HotExerciseTypeVO();
                h.setTypeId(String.valueOf(m.get("typeId")));
                h.setTypeName(String.valueOf(m.get("typeName")));
                h.setTypeIcon(m.get("typeIcon") == null ? null : String.valueOf(m.get("typeIcon")));
                h.setRecordCount(toLong(m.get("recordCount")));
                h.setTotalSeconds(toLong(m.get("totalSeconds")));
                hotList.add(h);
            }
            vo.setTopExerciseTypes(hotList);
        } else {
            vo.setTopExerciseTypes(Collections.emptyList());
        }
        return vo;
    }

    /* ========== 工具 ========== */

    private long daysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        long ms = end.getTime() - start.getTime();
        if (ms < 0) return -1;
        return ms / (1000L * 60 * 60 * 24);
    }

    private Date daysBefore(int n) {
        Date today = DateUtils.todayGMT8();
        Calendar c = Calendar.getInstance(DateUtils.GMT8);
        c.setTime(today);
        c.add(Calendar.DATE, -(n - 1));
        return c.getTime();
    }

    private int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return 0; }
    }

    private long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return 0L; }
    }
}