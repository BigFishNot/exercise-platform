package com.exercise.campus.service;

import com.exercise.campus.entity.dto.RecordFinishDTO;
import com.exercise.campus.entity.dto.RecordIdDTO;
import com.exercise.campus.entity.dto.RecordStartDTO;
import com.exercise.campus.entity.vo.ExerciseRecordVO;
import com.exercise.campus.entity.vo.RecordActiveVO;

import java.util.List;

/**
 * 运动记录 Service
 */
public interface ExerciseRecordService {

    /** 开始（创建 IN_PROGRESS） */
    String start(String userId, RecordStartDTO dto);

    /** 完成（actualSeconds >= planSeconds * 0.95 才标记 DONE，否则 ABANDONED） */
    ExerciseRecordVO finish(String userId, RecordFinishDTO dto);

    /** 主动放弃 */
    void abandon(String userId, RecordIdDTO dto);

    /** 当前进行中（无则返回 null） */
    RecordActiveVO getActive(String userId);

    /** 当日全部记录 */
    List<ExerciseRecordVO> listToday(String userId);

    /** 当日累计有效时长（秒） */
    Integer sumTodayActual(String userId);
}