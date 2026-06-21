package com.exercise.campus.service.impl;

import com.exercise.campus.converter.WeightGoalConverter;
import com.exercise.campus.entity.dto.WeightGoalAddDTO;
import com.exercise.campus.entity.dto.WeightGoalIdDTO;
import com.exercise.campus.entity.po.WeightGoal;
import com.exercise.campus.entity.query.WeightGoalQuery;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.enums.WeightGoalStatusEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.WeightGoalService;
import com.exercise.campus.utils.DateUtils;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.mappers.WeightGoalMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 减肥目标 Service 实现
 */
@Slf4j
@Service
public class WeightGoalServiceImpl implements WeightGoalService {

    @Autowired
    private WeightGoalMapper weightGoalMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(String userId, WeightGoalAddDTO dto) {
        validate(dto);
        // 业务层校验：同用户唯一 ACTIVE
        WeightGoal active = weightGoalMapper.selectActiveByUserId(userId);
        if (active != null) {
            throw new BusinessException(ResponseCodeEnum.GOAL_HAS_ACTIVE);
        }
        WeightGoal po = WeightGoalConverter.toPO(dto, userId);
        try {
            weightGoalMapper.insert(po);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // DB UNIQUE 兜底
            throw new BusinessException(ResponseCodeEnum.GOAL_HAS_ACTIVE);
        }
        log.info("[weightGoal:add] goalId={} userId={} target={}kg date={}",
                po.getGoalId(), userId, po.getTargetWeight(), po.getTargetDate());
        return po.getGoalId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String userId, WeightGoalAddDTO dto) {
        if (!StringUtils.hasText(dto.getRemark()) && dto.getTargetWeight() == null
                && dto.getTargetBodyFat() == null && dto.getTargetDate() == null) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "无有效修改字段");
        }
        if (dto.getTargetWeight() != null || dto.getTargetBodyFat() != null || dto.getTargetDate() != null) {
            validate(dto);
        }
        WeightGoal exists = weightGoalMapper.selectActiveByUserId(userId);
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.GOAL_NOT_ACTIVE);
        }
        // 只允许改 ACTIVE 的目标
        WeightGoal po = new WeightGoal();
        po.setGoalId(exists.getGoalId());
        po.setTargetWeight(dto.getTargetWeight());
        po.setTargetBodyFat(dto.getTargetBodyFat());
        po.setTargetDate(dto.getTargetDate());
        po.setRemark(dto.getRemark());
        weightGoalMapper.updateById(po);
        log.info("[weightGoal:update] goalId={}", exists.getGoalId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archive(String userId, WeightGoalIdDTO dto) {
        if (!StringUtils.hasText(dto.getGoalId())) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "goalId 必填");
        }
        WeightGoal exists = weightGoalMapper.selectById(dto.getGoalId());
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.GOAL_NOT_EXISTS);
        }
        if (!exists.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.FORBIDDEN);
        }
        if (!WeightGoalStatusEnum.ACTIVE.getStatus().equals(exists.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.GOAL_NOT_ACTIVE);
        }
        weightGoalMapper.updateArchive(dto.getGoalId(), null, new Date());
        log.info("[weightGoal:archive] goalId={} userId={}", dto.getGoalId(), userId);
    }

    @Override
    public WeightGoalVO getActive(String userId) {
        WeightGoal po = weightGoalMapper.selectActiveByUserId(userId);
        return WeightGoalConverter.toVO(po);
    }

    @Override
    public WeightGoalVO getById(String goalId) {
        WeightGoal po = weightGoalMapper.selectById(goalId);
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.GOAL_NOT_EXISTS);
        }
        return WeightGoalConverter.toVO(po);
    }

    @Override
    public List<WeightGoalVO> listByUser(String userId) {
        WeightGoalQuery q = new WeightGoalQuery();
        q.setUserId(userId);
        q.setPageSize(200);
        List<WeightGoal> list = weightGoalMapper.selectList(q);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(WeightGoalConverter::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResultVO<WeightGoalVO> pageList(WeightGoalQuery query) {
        Integer total = weightGoalMapper.countByQuery(query);
        if (total == null || total == 0) {
            return PageResultVO.empty();
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<WeightGoal> list = weightGoalMapper.selectList(query);
        List<WeightGoalVO> voList = list.stream().map(WeightGoalConverter::toVO).collect(Collectors.toList());
        return PageResultVO.of(new PageInfo<>(voList));
    }

    @Override
    public WeightGoal loadActive(String userId) {
        return weightGoalMapper.selectActiveByUserId(userId);
    }

    /* ========== 私有 ========== */

    private void validate(WeightGoalAddDTO dto) {
        if (dto.getTargetWeight() == null
                || dto.getTargetWeight().compareTo(BigDecimal.ZERO) < 0
                || dto.getTargetWeight().compareTo(new BigDecimal("500")) > 0) {
            throw new BusinessException(ResponseCodeEnum.GOAL_WEIGHT_INVALID);
        }
        if (dto.getTargetBodyFat() != null
                && (dto.getTargetBodyFat().compareTo(BigDecimal.ZERO) < 0
                || dto.getTargetBodyFat().compareTo(new BigDecimal("100")) > 0)) {
            throw new BusinessException(ResponseCodeEnum.GOAL_BODY_FAT_INVALID);
        }
        if (dto.getTargetDate() == null) {
            throw new BusinessException(ResponseCodeEnum.GOAL_DATE_INVALID);
        }
        Date today = DateUtils.todayGMT8();
        if (dto.getTargetDate().before(today)) {
            throw new BusinessException(ResponseCodeEnum.GOAL_DATE_INVALID);
        }
    }
}