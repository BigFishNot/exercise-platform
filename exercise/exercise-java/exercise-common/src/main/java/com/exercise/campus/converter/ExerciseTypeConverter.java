package com.exercise.campus.converter;

import com.exercise.campus.entity.dto.ExerciseTypeAddDTO;
import com.exercise.campus.entity.dto.ExerciseTypeUpdateDTO;
import com.exercise.campus.entity.po.ExerciseType;
import com.exercise.campus.entity.vo.ExerciseTypeAdminVO;
import com.exercise.campus.entity.vo.ExerciseTypeVO;
import com.exercise.campus.enums.StatusEnum;

import java.util.UUID;

/**
 * 运动类型转换器
 */
public class ExerciseTypeConverter {

    public static ExerciseType toPO(ExerciseTypeAddDTO dto) {
        ExerciseType po = new ExerciseType();
        po.setTypeId(UUID.randomUUID().toString().replace("-", ""));
        po.setName(dto.getName());
        po.setIcon(dto.getIcon());
        po.setDefaultSeconds(dto.getDefaultSeconds() == null ? 60 : dto.getDefaultSeconds());
        po.setDurationLevels(dto.getDurationLevels());
        po.setCaloriesPerMinute(dto.getCaloriesPerMinute() == null ? java.math.BigDecimal.ZERO : dto.getCaloriesPerMinute());
        po.setSort(dto.getSort() == null ? 0 : dto.getSort());
        po.setStatus(StatusEnum.ENABLED.getStatus());
        return po;
    }

    public static ExerciseType toPO(ExerciseTypeUpdateDTO dto) {
        ExerciseType po = new ExerciseType();
        po.setTypeId(dto.getTypeId());
        po.setName(dto.getName());
        po.setIcon(dto.getIcon());
        po.setDefaultSeconds(dto.getDefaultSeconds());
        po.setDurationLevels(dto.getDurationLevels());
        po.setCaloriesPerMinute(dto.getCaloriesPerMinute());
        po.setSort(dto.getSort());
        return po;
    }

    public static ExerciseTypeVO toVO(ExerciseType po) {
        if (po == null) return null;
        ExerciseTypeVO vo = new ExerciseTypeVO();
        vo.setTypeId(po.getTypeId());
        vo.setName(po.getName());
        vo.setIcon(po.getIcon());
        vo.setDefaultSeconds(po.getDefaultSeconds());
        vo.setCaloriesPerMinute(po.getCaloriesPerMinute());
        vo.setDurationLevels(po.getDurationLevels());
        vo.setStatusName(po.getStatus() != null
                ? StatusEnum.of(po.getStatus()).getDesc()
                : null);
        vo.setUpdateTime(po.getUpdateTime());
        return vo;
    }

    public static ExerciseTypeAdminVO toAdminVO(ExerciseType po, Integer refCount) {
        if (po == null) return null;
        ExerciseTypeAdminVO vo = new ExerciseTypeAdminVO();
        vo.setTypeId(po.getTypeId());
        vo.setName(po.getName());
        vo.setIcon(po.getIcon());
        vo.setDefaultSeconds(po.getDefaultSeconds());
        vo.setDurationLevels(po.getDurationLevels());
        vo.setCaloriesPerMinute(po.getCaloriesPerMinute());
        vo.setSort(po.getSort());
        vo.setStatus(po.getStatus());
        vo.setStatusName(po.getStatus() != null
                ? StatusEnum.of(po.getStatus()).getDesc()
                : null);
        vo.setRefCount(refCount == null ? 0 : refCount);
        vo.setCreateTime(po.getCreateTime());
        vo.setUpdateTime(po.getUpdateTime());
        return vo;
    }
}