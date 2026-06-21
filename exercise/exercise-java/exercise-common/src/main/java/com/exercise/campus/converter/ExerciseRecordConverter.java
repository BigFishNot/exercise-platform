package com.exercise.campus.converter;

import com.exercise.campus.entity.dto.RecordStartDTO;
import com.exercise.campus.entity.po.ExerciseRecord;
import com.exercise.campus.entity.po.ExerciseType;
import com.exercise.campus.entity.vo.ExerciseRecordVO;
import com.exercise.campus.entity.vo.RecordActiveVO;
import com.exercise.campus.enums.ExerciseRecordStatusEnum;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * 运动记录 Converter
 */
public class ExerciseRecordConverter {

    /** 起手：仅写初始字段；calories/actualSeconds/endTime 在 finish 时回填 */
    public static ExerciseRecord toStartPO(RecordStartDTO dto, String userId, ExerciseType type) {
        ExerciseRecord po = new ExerciseRecord();
        po.setRecordId(UUID.randomUUID().toString().replace("-", ""));
        po.setUserId(userId);
        po.setTypeId(type.getTypeId());
        po.setTypeName(type.getName());
        po.setPlanSeconds(dto.getPlanSeconds());
        po.setActualSeconds(0);
        po.setCalories(BigDecimal.ZERO);
        po.setExerciseDate(todayGMT8());
        po.setStartTime(new Date());
        po.setEndTime(null);
        po.setStatus(ExerciseRecordStatusEnum.IN_PROGRESS.getStatus());
        po.setRemark(dto.getRemark());
        return po;
    }

    public static ExerciseRecordVO toVO(ExerciseRecord po, String typeIcon) {
        if (po == null) return null;
        ExerciseRecordVO vo = new ExerciseRecordVO();
        vo.setRecordId(po.getRecordId());
        vo.setUserId(po.getUserId());
        vo.setTypeId(po.getTypeId());
        vo.setTypeName(po.getTypeName());
        vo.setTypeIcon(typeIcon);
        vo.setPlanSeconds(po.getPlanSeconds());
        vo.setActualSeconds(po.getActualSeconds());
        vo.setCalories(po.getCalories());
        vo.setExerciseDate(po.getExerciseDate());
        vo.setStartTime(po.getStartTime());
        vo.setEndTime(po.getEndTime());
        vo.setStatus(po.getStatus());
        vo.setStatusName(po.getStatus() != null
                ? ExerciseRecordStatusEnum.of(po.getStatus()).getDesc() : null);
        vo.setAbandonReason(po.getAbandonReason());
        vo.setRemark(po.getRemark());
        return vo;
    }

    public static RecordActiveVO toActiveVO(ExerciseRecord po, String typeIcon) {
        if (po == null) return null;
        RecordActiveVO vo = new RecordActiveVO();
        vo.setRecordId(po.getRecordId());
        vo.setTypeId(po.getTypeId());
        vo.setTypeName(po.getTypeName());
        vo.setTypeIcon(typeIcon);
        vo.setPlanSeconds(po.getPlanSeconds());
        vo.setStartTime(po.getStartTime());
        return vo;
    }

    /** 服务器 GMT+8 当日 00:00:00 */
    public static Date todayGMT8() {
        Calendar c = Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT+8"));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}