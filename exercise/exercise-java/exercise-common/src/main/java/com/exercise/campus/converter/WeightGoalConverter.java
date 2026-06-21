package com.exercise.campus.converter;

import com.exercise.campus.entity.dto.WeightGoalAddDTO;
import com.exercise.campus.entity.po.WeightGoal;
import com.exercise.campus.entity.vo.WeightGoalVO;
import com.exercise.campus.enums.WeightGoalStatusEnum;

import java.util.UUID;

/**
 * 减肥目标 Converter
 */
public class WeightGoalConverter {

    public static WeightGoal toPO(WeightGoalAddDTO dto, String userId) {
        WeightGoal po = new WeightGoal();
        po.setGoalId(UUID.randomUUID().toString().replace("-", ""));
        po.setUserId(userId);
        po.setTargetWeight(dto.getTargetWeight());
        po.setTargetBodyFat(dto.getTargetBodyFat());
        po.setTargetDate(dto.getTargetDate());
        po.setRemark(dto.getRemark());
        po.setStatus(WeightGoalStatusEnum.ACTIVE.getStatus());
        po.setActiveMarker(userId); // ACTIVE 时与 userId 相同
        return po;
    }

    public static WeightGoalVO toVO(WeightGoal po) {
        if (po == null) return null;
        WeightGoalVO vo = new WeightGoalVO();
        vo.setGoalId(po.getGoalId());
        vo.setUserId(po.getUserId());
        vo.setTargetWeight(po.getTargetWeight());
        vo.setTargetBodyFat(po.getTargetBodyFat());
        vo.setTargetDate(po.getTargetDate());
        vo.setRemark(po.getRemark());
        vo.setStatus(po.getStatus());
        vo.setStatusName(po.getStatus() != null
                ? WeightGoalStatusEnum.of(po.getStatus()).getDesc() : null);
        vo.setArchiveTime(po.getArchiveTime());
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }
}