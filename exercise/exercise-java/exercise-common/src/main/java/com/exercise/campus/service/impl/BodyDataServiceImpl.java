package com.exercise.campus.service.impl;

import com.exercise.campus.converter.BodyDataConverter;
import com.exercise.campus.entity.dto.BodyDataAddDTO;
import com.exercise.campus.entity.po.BodyData;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.query.BodyDataTrendQuery;
import com.exercise.campus.entity.vo.BodyDataSummaryVO;
import com.exercise.campus.entity.vo.BodyDataTrendVO;
import com.exercise.campus.entity.vo.BodyDataVO;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.BodyDataService;
import com.exercise.campus.service.ExercisePlanService;
import com.exercise.campus.service.UserInfoService;
import com.exercise.campus.utils.DateUtils;
import com.exercise.mappers.BodyDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 身体数据 Service 实现
 */
@Slf4j
@Service
public class BodyDataServiceImpl implements BodyDataService {

    @Autowired
    private BodyDataMapper bodyDataMapper;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ExercisePlanService exercisePlanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsert(String userId, BodyDataAddDTO dto) {
        if (dto.getWeight() == null
                || dto.getWeight().compareTo(BigDecimal.ZERO) < 0
                || dto.getWeight().compareTo(new BigDecimal("500")) > 0) {
            throw new BusinessException(ResponseCodeEnum.BODY_DATA_WEIGHT_INVALID);
        }
        UserInfo user = userInfoService.getById(userId);
        if (user.getHeight() == null || user.getHeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResponseCodeEnum.BODY_DATA_HEIGHT_REQUIRED);
        }
        BodyData po = BodyDataConverter.toPO(dto, userId);
        po.setBmi(BodyDataConverter.computeBmi(dto.getWeight(), user.getHeight()));
        bodyDataMapper.upsert(po);
        // upsert 之后 po.id 可能为 0，重新查一次拿自增 id
        BodyData reloaded = bodyDataMapper.selectToday(userId, po.getRecordDate());
        log.info("[bodyData:upsert] userId={} date={} weight={}kg bmi={}",
                userId, po.getRecordDate(), dto.getWeight(), po.getBmi());
        return reloaded == null ? null : reloaded.getId();
    }

    @Override
    public BodyDataVO getToday(String userId) {
        BodyData po = bodyDataMapper.selectToday(userId, DateUtils.todayGMT8());
        return BodyDataConverter.toVO(po);
    }

    @Override
    public BodyDataVO getById(Long id) {
        return BodyDataConverter.toVO(bodyDataMapper.selectById(id));
    }

    @Override
    public TrendResult getTrend(String userId, BodyDataTrendQuery query) {
        // 1. 计算区间
        Date[] range = resolveRange(userId, query);
        if (range == null) {
            return new TrendResult(new BodyDataSummaryVO(), Collections.emptyList());
        }
        // 2. 拉取趋势
        List<BodyData> pos = bodyDataMapper.selectTrendByRange(userId, range[0], range[1]);
        // 3. 汇总
        UserInfo user = userInfoService.getById(userId);
        BigDecimal height = user == null ? null : user.getHeight();
        BodyDataSummaryVO summary = BodyDataConverter.toSummaryVO(pos, height);
        List<BodyDataTrendVO> points = BodyDataConverter.toTrendVOList(pos);
        return new TrendResult(summary, points);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        bodyDataMapper.deleteById(id);
    }

    @Override
    public BodyData loadTodayPO(String userId) {
        return bodyDataMapper.selectToday(userId, DateUtils.todayGMT8());
    }

    /* ========== 私有 ========== */

    private Date[] resolveRange(String userId, BodyDataTrendQuery query) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        fmt.setTimeZone(DateUtils.GMT8);
        String range = query.getRange() == null ? "30d" : query.getRange();
        try {
            switch (range) {
                case "7d":  return lastNDays(7);
                case "30d": return lastNDays(30);
                case "90d": return lastNDays(90);
                case "stage": {
                    com.exercise.campus.entity.po.ExercisePlan plan =
                            exercisePlanService.loadOngoing(userId);
                    if (plan == null || plan.getStartDate() == null || plan.getEndDate() == null) {
                        return lastNDays(30);
                    }
                    return new Date[] {
                            DateUtils.stripTime(plan.getStartDate()),
                            DateUtils.stripTime(plan.getEndDate())
                    };
                }
                case "custom": {
                    if (query.getStartDate() == null || query.getEndDate() == null) {
                        return lastNDays(30);
                    }
                    return new Date[] { fmt.parse(query.getStartDate()), fmt.parse(query.getEndDate()) };
                }
                default: return lastNDays(30);
            }
        } catch (Exception e) {
            return lastNDays(30);
        }
    }

    private Date[] lastNDays(int n) {
        Date end = DateUtils.todayGMT8();
        Calendar c = Calendar.getInstance(DateUtils.GMT8);
        c.setTime(end);
        c.add(Calendar.DATE, -(n - 1));
        return new Date[] { c.getTime(), end };
    }
}