package com.exercise.campus.service.impl;

import com.exercise.campus.converter.PlanConverter;
import com.exercise.campus.entity.dto.PlanAddDTO;
import com.exercise.campus.entity.dto.PlanUpdateDailyTargetDTO;
import com.exercise.campus.entity.po.ExercisePlan;
import com.exercise.campus.entity.query.ExercisePlanQuery;
import com.exercise.campus.entity.vo.PlanCalendarDayVO;
import com.exercise.campus.entity.vo.PlanCalendarVO;
import com.exercise.campus.entity.vo.PlanVO;
import com.exercise.campus.enums.PlanStatusEnum;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.ExercisePlanService;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.mappers.ExercisePlanMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 阶段计划 Service 实现
 */
@Slf4j
@Service
public class ExercisePlanServiceImpl implements ExercisePlanService {

    @Autowired
    private ExercisePlanMapper exercisePlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(String userId, PlanAddDTO dto) {
        validateDates(dto.getStartDate(), dto.getEndDate());
        validateTargetMinutes(dto.getDailyTargetMinutes());

        // 同用户唯一进行中校验（DB UNIQUE 兜底 + 业务层先查）
        ExercisePlan ongoing = exercisePlanMapper.selectOngoingByUserId(userId);
        if (ongoing != null) {
            throw new BusinessException(ResponseCodeEnum.PLAN_HAS_ONGOING);
        }

        ExercisePlan po = PlanConverter.toPO(dto, userId);
        try {
            exercisePlanMapper.insert(po);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 兜底：UNIQUE(ongoing_unique) 冲突
            throw new BusinessException(ResponseCodeEnum.PLAN_HAS_ONGOING);
        }
        log.info("[exercisePlan:add] planId={} userId={} days={}",
                po.getPlanId(), userId, PlanConverter.daysBetween(dto.getStartDate(), dto.getEndDate()) + 1);
        return po.getPlanId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDailyTarget(String userId, PlanUpdateDailyTargetDTO dto) {
        if (!StringUtils.hasText(dto.getPlanId())) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "planId 必填");
        }
        ExercisePlan exists = exercisePlanMapper.selectById(dto.getPlanId());
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.PLAN_NOT_EXISTS);
        }
        if (!exists.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.FORBIDDEN);
        }
        if (!PlanStatusEnum.ONGOING.getStatus().equals(exists.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.PLAN_NOT_ONGOING);
        }
        validateTargetMinutes(dto.getDailyTargetMinutes());
        ExercisePlan po = new ExercisePlan();
        po.setPlanId(dto.getPlanId());
        po.setDailyTargetMinutes(dto.getDailyTargetMinutes());
        po.setRemark(dto.getRemark());
        exercisePlanMapper.updateById(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String userId, String planId) {
        ExercisePlan exists = exercisePlanMapper.selectById(planId);
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.PLAN_NOT_EXISTS);
        }
        if (!exists.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.FORBIDDEN);
        }
        if (!PlanStatusEnum.ONGOING.getStatus().equals(exists.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.PLAN_NOT_ONGOING);
        }
        try {
            exercisePlanMapper.updateStatus(planId,
                    PlanStatusEnum.CANCELED.getStatus(), null, new Date());
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new BusinessException(ResponseCodeEnum.PLAN_HAS_ONGOING);
        }
        log.info("[exercisePlan:cancel] planId={} userId={}", planId, userId);
    }

    @Override
    public PlanVO getCurrent(String userId) {
        ExercisePlan ongoing = exercisePlanMapper.selectOngoingByUserId(userId);
        if (ongoing == null) return null;
        return PlanConverter.toVO(ongoing);
    }

    @Override
    public List<PlanVO> listByUser(String userId) {
        ExercisePlanQuery q = new ExercisePlanQuery();
        q.setUserId(userId);
        q.setPageSize(200);
        List<ExercisePlan> list = exercisePlanMapper.selectList(q);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        List<PlanVO> voList = new ArrayList<>(list.size());
        for (ExercisePlan p : list) voList.add(PlanConverter.toVO(p));
        return voList;
    }

    @Override
    public PlanCalendarVO getCalendar(String userId, String planId) {
        ExercisePlan po = exercisePlanMapper.selectById(planId);
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.PLAN_NOT_EXISTS);
        }
        if (!po.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.FORBIDDEN);
        }
        List<PlanCalendarDayVO> days = buildCalendarDays(po);
        return PlanConverter.toCalendarVO(po, days);
    }

    @Override
    public PlanVO detail(String planId) {
        ExercisePlan po = exercisePlanMapper.selectById(planId);
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.PLAN_NOT_EXISTS);
        }
        return PlanConverter.toVO(po);
    }

    @Override
    public PageResultVO<PlanVO> pageList(ExercisePlanQuery query) {
        Integer total = exercisePlanMapper.countByQuery(query);
        if (total == null || total == 0) {
            return PageResultVO.empty();
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ExercisePlan> list = exercisePlanMapper.selectList(query);
        List<PlanVO> voList = new ArrayList<>(list.size());
        for (ExercisePlan p : list) voList.add(PlanConverter.toVO(p));
        return PageResultVO.of(new PageInfo<>(voList));
    }

    @Override
    public ExercisePlan loadById(String planId) {
        return exercisePlanMapper.selectById(planId);
    }

    /* ========== 私有 ========== */

    private void validateDates(Date start, Date end) {
        if (start == null || end == null) {
            throw new BusinessException(ResponseCodeEnum.PLAN_DATE_INVALID);
        }
        Date today = stripTime(new Date());
        if (start.before(today)) {
            throw new BusinessException(ResponseCodeEnum.PLAN_DATE_INVALID);
        }
        if (!end.after(start)) {
            throw new BusinessException(ResponseCodeEnum.PLAN_DATE_INVALID);
        }
        long days = PlanConverter.daysBetween(start, end) + 1;
        if (days < 1 || days > 60) {
            throw new BusinessException(ResponseCodeEnum.PLAN_DATE_INVALID);
        }
    }

    private void validateTargetMinutes(Integer minutes) {
        if (minutes == null || minutes < 1 || minutes > 600) {
            throw new BusinessException(ResponseCodeEnum.PLAN_TARGET_INVALID);
        }
    }

    private Date stripTime(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private List<PlanCalendarDayVO> buildCalendarDays(ExercisePlan po) {
        List<PlanCalendarDayVO> list = new ArrayList<>();
        if (po.getStartDate() == null || po.getEndDate() == null) return list;
        Calendar c = Calendar.getInstance();
        c.setTime(po.getStartDate());
        Date end = po.getEndDate();
        Integer target = po.getDailyTargetMinutes();
        Date today = stripTime(new Date());
        while (!c.getTime().after(end)) {
            Date cur = c.getTime();
            PlanCalendarDayVO d = new PlanCalendarDayVO();
            d.setDate(cur);
            d.setTargetMinutes(target);
            d.setActualMinutes(0);
            // exerciseCheckIn 模块接入前先全部置为 NOT_DONE
            d.setCheckInStatus(cur.after(today) ? "FUTURE" : "NOT_DONE");
            list.add(d);
            c.add(Calendar.DATE, 1);
        }
        return list;
    }
}