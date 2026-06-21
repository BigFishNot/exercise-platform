package com.exercise.campus.service.impl;

import com.exercise.campus.converter.ExerciseRecordConverter;
import com.exercise.campus.entity.dto.RecordFinishDTO;
import com.exercise.campus.entity.dto.RecordIdDTO;
import com.exercise.campus.entity.dto.RecordStartDTO;
import com.exercise.campus.entity.po.ExerciseRecord;
import com.exercise.campus.entity.po.ExerciseType;
import com.exercise.campus.entity.vo.ExerciseRecordVO;
import com.exercise.campus.entity.vo.RecordActiveVO;
import com.exercise.campus.enums.ExerciseRecordStatusEnum;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.enums.StatusEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.ExerciseRecordService;
import com.exercise.campus.service.ExerciseTypeService;
import com.exercise.mappers.ExerciseRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运动记录 Service 实现
 */
@Slf4j
@Service
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Autowired
    private ExerciseTypeService exerciseTypeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String start(String userId, RecordStartDTO dto) {
        validatePlanSeconds(dto.getPlanSeconds());
        ExerciseType type = exerciseTypeService.getById(dto.getTypeId());
        if (!StatusEnum.ENABLED.getStatus().equals(type.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.RECORD_TYPE_DISABLED);
        }
        // 同一用户同一时刻只能有一条 IN_PROGRESS
        ExerciseRecord active = exerciseRecordMapper.selectActiveByUserId(userId);
        if (active != null) {
            throw new BusinessException(ResponseCodeEnum.RECORD_HAS_ACTIVE);
        }
        ExerciseRecord po = ExerciseRecordConverter.toStartPO(dto, userId, type);
        exerciseRecordMapper.insert(po);
        log.info("[record:start] recordId={} userId={} typeId={} plan={}s",
                po.getRecordId(), userId, po.getTypeId(), po.getPlanSeconds());
        return po.getRecordId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExerciseRecordVO finish(String userId, RecordFinishDTO dto) {
        if (!StringUtils.hasText(dto.getRecordId())) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "recordId 必填");
        }
        if (dto.getActualSeconds() == null || dto.getActualSeconds() < 0) {
            throw new BusinessException(ResponseCodeEnum.RECORD_DURATION_INVALID);
        }
        ExerciseRecord exists = exerciseRecordMapper.selectById(dto.getRecordId());
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.RECORD_NOT_EXISTS);
        }
        if (!exists.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.FORBIDDEN);
        }
        if (!ExerciseRecordStatusEnum.IN_PROGRESS.getStatus().equals(exists.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.RECORD_NOT_IN_PROGRESS);
        }
        Integer plan = exists.getPlanSeconds();
        int actual = Math.min(dto.getActualSeconds(), plan); // 防止前端累计超过计划太多
        // 判定：DONE 当 actual >= plan * 0.95，否则 ABANDONED
        int threshold = (int) Math.floor(plan * 0.95);
        int finalStatus = actual >= threshold
                ? ExerciseRecordStatusEnum.DONE.getStatus()
                : ExerciseRecordStatusEnum.ABANDONED.getStatus();

        // 卡路里 = actual / 60 * 每分钟消耗参考
        ExerciseType type = exerciseTypeService.getById(exists.getTypeId());
        BigDecimal calories = computeCalories(actual, type.getCaloriesPerMinute());

        exerciseRecordMapper.updateFinish(dto.getRecordId(), actual, calories, finalStatus, new Date());

        log.info("[record:finish] recordId={} actual={}s plan={}s status={} cal={}",
                dto.getRecordId(), actual, plan, finalStatus, calories);

        // 回查带出状态
        ExerciseRecord updated = exerciseRecordMapper.selectById(dto.getRecordId());
        return ExerciseRecordConverter.toVO(updated, type.getIcon());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void abandon(String userId, RecordIdDTO dto) {
        if (!StringUtils.hasText(dto.getRecordId())) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "recordId 必填");
        }
        ExerciseRecord exists = exerciseRecordMapper.selectById(dto.getRecordId());
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.RECORD_NOT_EXISTS);
        }
        if (!exists.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.FORBIDDEN);
        }
        if (!ExerciseRecordStatusEnum.IN_PROGRESS.getStatus().equals(exists.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.RECORD_NOT_IN_PROGRESS);
        }
        String reason = StringUtils.hasText(dto.getAbandonReason()) ? dto.getAbandonReason() : "USER_MANUAL";
        exerciseRecordMapper.updateAbandon(dto.getRecordId(), reason,
                ExerciseRecordStatusEnum.ABANDONED.getStatus(), new Date());
        log.info("[record:abandon] recordId={} reason={}", dto.getRecordId(), reason);
    }

    @Override
    public RecordActiveVO getActive(String userId) {
        ExerciseRecord po = exerciseRecordMapper.selectActiveByUserId(userId);
        if (po == null) return null;
        ExerciseType type = exerciseTypeService.getById(po.getTypeId());
        return ExerciseRecordConverter.toActiveVO(po, type == null ? null : type.getIcon());
    }

    @Override
    public List<ExerciseRecordVO> listToday(String userId) {
        Date today = ExerciseRecordConverter.todayGMT8();
        List<ExerciseRecord> list = exerciseRecordMapper.selectTodayByUserId(userId, today);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(po -> {
            ExerciseType type = exerciseTypeService.getById(po.getTypeId());
            return ExerciseRecordConverter.toVO(po, type == null ? null : type.getIcon());
        }).collect(Collectors.toList());
    }

    @Override
    public Integer sumTodayActual(String userId) {
        Date today = ExerciseRecordConverter.todayGMT8();
        Integer sum = exerciseRecordMapper.sumTodayActualSeconds(userId, today);
        return sum == null ? 0 : sum;
    }

    /* ========== 私有 ========== */

    private void validatePlanSeconds(Integer plan) {
        if (plan == null || plan < 10 || plan > 7200) {
            throw new BusinessException(ResponseCodeEnum.RECORD_DURATION_INVALID);
        }
    }

    private BigDecimal computeCalories(int actualSeconds, BigDecimal caloriesPerMinute) {
        if (caloriesPerMinute == null) return BigDecimal.ZERO;
        return caloriesPerMinute
                .multiply(BigDecimal.valueOf(actualSeconds))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}