package com.exercise.campus.service.impl;

import com.exercise.campus.converter.ExerciseTypeConverter;
import com.exercise.campus.entity.dto.ExerciseTypeAddDTO;
import com.exercise.campus.entity.dto.ExerciseTypeUpdateDTO;
import com.exercise.campus.entity.po.ExerciseType;
import com.exercise.campus.entity.query.ExerciseTypeQuery;
import com.exercise.campus.entity.vo.ExerciseTypeAdminVO;
import com.exercise.campus.entity.vo.ExerciseTypeVO;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.enums.StatusEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.ExerciseTypeService;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.mappers.ExerciseTypeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运动类型 Service 实现
 */
@Slf4j
@Service
public class ExerciseTypeServiceImpl implements ExerciseTypeService {

    @Autowired
    private ExerciseTypeMapper exerciseTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(ExerciseTypeAddDTO dto) {
        validate(dto.getName(), dto.getDefaultSeconds(), dto.getCaloriesPerMinute());
        if (exerciseTypeMapper.countByName(dto.getName()) > 0) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_NAME_DUPLICATE);
        }
        ExerciseType po = ExerciseTypeConverter.toPO(dto);
        exerciseTypeMapper.insert(po);
        log.info("[exerciseType:add] typeId={} name={}", po.getTypeId(), po.getName());
        return po.getTypeId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExerciseTypeUpdateDTO dto) {
        if (!StringUtils.hasText(dto.getTypeId())) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "typeId 必填");
        }
        ExerciseType exists = exerciseTypeMapper.selectById(dto.getTypeId());
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_NOT_EXISTS);
        }
        validate(dto.getName(), dto.getDefaultSeconds(), dto.getCaloriesPerMinute());
        // 重名校验：仅当名称发生变化时校验
        if (StringUtils.hasText(dto.getName()) && !dto.getName().equals(exists.getName())) {
            if (exerciseTypeMapper.countByName(dto.getName()) > 0) {
                throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_NAME_DUPLICATE);
            }
        }
        ExerciseType po = ExerciseTypeConverter.toPO(dto);
        exerciseTypeMapper.updateById(po);
        log.info("[exerciseType:update] typeId={}", po.getTypeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String typeId, Integer status) {
        ExerciseType exists = exerciseTypeMapper.selectById(typeId);
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_NOT_EXISTS);
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "状态值非法");
        }
        exerciseTypeMapper.updateStatus(typeId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String typeId) {
        ExerciseType exists = exerciseTypeMapper.selectById(typeId);
        if (exists == null) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_NOT_EXISTS);
        }
        Integer ref = exerciseTypeMapper.countRef(typeId);
        if (ref != null && ref > 0) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_REFERENCED);
        }
        exerciseTypeMapper.deleteById(typeId);
    }

    @Override
    public ExerciseType getById(String typeId) {
        ExerciseType po = exerciseTypeMapper.selectById(typeId);
        if (po == null) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_NOT_EXISTS);
        }
        return po;
    }

    @Override
    public List<ExerciseTypeVO> listEnabled() {
        List<ExerciseType> list = exerciseTypeMapper.selectEnabled();
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(ExerciseTypeConverter::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResultVO<ExerciseTypeAdminVO> pageList(ExerciseTypeQuery query) {
        Integer total = exerciseTypeMapper.countByQuery(query);
        if (total == null || total == 0) {
            return PageResultVO.empty();
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ExerciseType> list = exerciseTypeMapper.selectList(query);
        List<ExerciseTypeAdminVO> voList = list.stream().map(po -> {
            Integer ref = exerciseTypeMapper.countRef(po.getTypeId());
            return ExerciseTypeConverter.toAdminVO(po, ref);
        }).collect(Collectors.toList());
        return PageResultVO.of(new PageInfo<>(voList));
    }

    /* ========== 私有 ========== */

    private void validate(String name, Integer defaultSeconds, java.math.BigDecimal caloriesPerMinute) {
        if (!StringUtils.hasText(name) || name.length() > 64) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_PARAM_INVALID, "名称不能为空且不超过 64 字符");
        }
        if (defaultSeconds != null && (defaultSeconds < 10 || defaultSeconds > 7200)) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_PARAM_INVALID, "默认时长需在 10-7200 秒之间");
        }
        if (caloriesPerMinute != null && caloriesPerMinute.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResponseCodeEnum.EXERCISE_TYPE_PARAM_INVALID, "卡路里不能为负");
        }
    }
}