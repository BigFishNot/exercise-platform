package com.exercise.campus.service.impl;

import com.exercise.campus.converter.CheckInConverter;
import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.query.CheckInAdminQuery;
import com.exercise.campus.entity.vo.CheckInAdminVO;
import com.exercise.campus.entity.vo.CheckInTodayVO;
import com.exercise.campus.entity.vo.PlanCalendarDayVO;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.service.ExerciseCheckInService;
import com.exercise.campus.service.ExercisePlanService;
import com.exercise.campus.utils.DateUtils;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.mappers.ExerciseRecordMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 打卡判定 Service 实现（按需计算）
 */
@Slf4j
@Service
public class ExerciseCheckInServiceImpl implements ExerciseCheckInService {

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Autowired
    private ExercisePlanService exercisePlanService;

    @Override
    public CheckInTodayVO getToday(String userId) {
        ExercisePlan ongoing = exercisePlanService.loadOngoing(userId);
        if (ongoing == null) {
            return CheckInConverter.toTodayVO(null, 0);
        }
        Date today = DateUtils.todayGMT8();
        Integer sum = exerciseRecordMapper.sumTodayActualSeconds(userId, today);
        int actualMin = (sum == null ? 0 : sum) / 60;
        return CheckInConverter.toTodayVO(ongoing, actualMin);
    }

    @Override
    public List<PlanCalendarDayVO> getPlanCalendar(ExercisePlan plan) {
        if (plan == null || plan.getStartDate() == null || plan.getEndDate() == null) {
            return Collections.emptyList();
        }
        Date today = DateUtils.todayGMT8();
        // 拉取区间内每天的累计实际秒数（仅 DONE）
        List<Map<String, Object>> rows = exerciseRecordMapper.sumActualSecondsByDateRange(
                plan.getUserId(), plan.getStartDate(), plan.getEndDate());
        // 关键: 统一用 "yyyy-MM-dd" 格式化, 避免 Date.toString() 出现 "Sat Jun 21..." 等格式
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        fmt.setTimeZone(DateUtils.GMT8);
        final SimpleDateFormat fmtFinal = fmt;
        Map<String, Integer> actualByDate = rows.stream().collect(Collectors.toMap(
                r -> {
                    Object d = r.get("date");
                    if (d instanceof Date) return fmtFinal.format((Date) d);
                    return String.valueOf(d);
                },
                r -> {
                    Object v = r.get("total");
                    return v == null ? 0 : ((Number) v).intValue();
                },
                (a, b) -> a
        ));

        int target = plan.getDailyTargetMinutes() == null ? 0 : plan.getDailyTargetMinutes();
        Date cur = DateUtils.stripTime(plan.getStartDate());
        Date end = DateUtils.stripTime(plan.getEndDate());
        List<PlanCalendarDayVO> days = new ArrayList<>();
        while (!cur.after(end)) {
            String key = fmtFinal.format(cur);
            int totalSec = actualByDate.getOrDefault(key, 0);
            int actualMin = totalSec / 60;
            boolean isFuture = cur.after(today);
            days.add(CheckInConverter.toDayVO(cur, actualMin, target, isFuture));
            Calendar c = Calendar.getInstance(DateUtils.GMT8);
            c.setTime(cur);
            c.add(Calendar.DATE, 1);
            cur = c.getTime();
        }
        return days;
    }

    @Override
    public void onRecordFinished(String userId, Date exerciseDate) {
        // 当前按需计算，无需主动重算；保留接口供后续定时任务 / 缓存表接入
        log.debug("[checkIn:onRecordFinished] userId={} date={} (no-op, on-demand compute)", userId, exerciseDate);
    }

    @Override
    public PageResultVO<CheckInAdminVO> pageList(CheckInAdminQuery query) {
        // 简单实现：按用户分组取其计划
        // 这里复用 exercisePlanService 的 listByUser，再为每条计划聚合区间内每日 done / insufficient
        // 为简化，本次先做内存聚合
        List<PlanVO> plans = queryUserPlans(query);
        if (plans == null || plans.isEmpty()) {
            return PageResultVO.empty();
        }
        // 分页
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<CheckInAdminVO> voList = new ArrayList<>();
        for (PlanVO p : plans) {
            // 将 PlanVO 转为 PO 调用 getPlanCalendar（getPlanCalendar 字段兼容）
            ExercisePlan po = new ExercisePlan();
            po.setPlanId(p.getPlanId());
            po.setUserId(p.getUserId());
            po.setStartDate(p.getStartDate());
            po.setEndDate(p.getEndDate());
            po.setDailyTargetMinutes(p.getDailyTargetMinutes());

            List<PlanCalendarDayVO> days = getPlanCalendar(po);
            int done = 0, ins = 0;
            for (PlanCalendarDayVO d : days) {
                if ("DONE".equals(d.getCheckInStatus())) done++;
                else if ("INSUFFICIENT".equals(d.getCheckInStatus())) ins++;
            }
            CheckInAdminVO vo = CheckInConverter.toAdminVO(po, done, ins);
            voList.add(vo);
        }
        return PageResultVO.of(new PageInfo<>(voList));
    }

    private List<PlanVO> queryUserPlans(CheckInAdminQuery query) {
        if (query.getUserId() != null && !query.getUserId().isEmpty()) {
            return exercisePlanService.listByUser(query.getUserId());
        }
        // 简化：admin 端无 userId 时不查所有用户（避免全表扫描）
        // 实际生产应分页查 user 表后再展开
        return Collections.emptyList();
    }
}