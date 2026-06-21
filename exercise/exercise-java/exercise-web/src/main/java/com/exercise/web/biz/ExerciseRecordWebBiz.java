package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.entity.dto.RecordFinishDTO;
import com.exercise.campus.entity.dto.RecordIdDTO;
import com.exercise.campus.entity.dto.RecordStartDTO;
import com.exercise.campus.entity.vo.ExerciseRecordVO;
import com.exercise.campus.entity.vo.RecordActiveVO;
import com.exercise.campus.service.ExerciseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 运动记录 Web Biz
 */
@Component
public class ExerciseRecordWebBiz {

    @Autowired
    private ExerciseRecordService exerciseRecordService;

    public String start(RecordStartDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        return exerciseRecordService.start(userId, dto);
    }

    public ExerciseRecordVO finish(RecordFinishDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        return exerciseRecordService.finish(userId, dto);
    }

    public void abandon(RecordIdDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        exerciseRecordService.abandon(userId, dto);
    }

    public RecordActiveVO getActive() {
        String userId = LoginContextHolder.requireUserId();
        return exerciseRecordService.getActive(userId);
    }

    public List<ExerciseRecordVO> listToday() {
        String userId = LoginContextHolder.requireUserId();
        return exerciseRecordService.listToday(userId);
    }

    public Integer sumTodayActual() {
        String userId = LoginContextHolder.requireUserId();
        return exerciseRecordService.sumTodayActual(userId);
    }
}