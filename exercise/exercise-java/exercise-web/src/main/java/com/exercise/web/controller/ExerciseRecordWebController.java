package com.exercise.web.controller;

import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.RecordFinishDTO;
import com.exercise.campus.entity.dto.RecordIdDTO;
import com.exercise.campus.entity.dto.RecordStartDTO;
import com.exercise.campus.entity.vo.ExerciseRecordVO;
import com.exercise.campus.entity.vo.RecordActiveVO;
import com.exercise.campus.vo.ResponseVO;
import com.exercise.web.biz.ExerciseRecordWebBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 运动记录 Web Controller
 * 路径：/api/exerciseRecord/*
 */
@RestController
@RequestMapping("/exerciseRecord")
public class ExerciseRecordWebController extends BaseController {

    @Autowired
    private ExerciseRecordWebBiz exerciseRecordWebBiz;

    @PostMapping("/start")
    public ResponseVO<String> start(@RequestBody RecordStartDTO dto) {
        return success(exerciseRecordWebBiz.start(dto));
    }

    @PostMapping("/finish")
    public ResponseVO<ExerciseRecordVO> finish(@RequestBody RecordFinishDTO dto) {
        return success(exerciseRecordWebBiz.finish(dto));
    }

    @PostMapping("/abandon")
    public ResponseVO<Void> abandon(@RequestBody RecordIdDTO dto) {
        exerciseRecordWebBiz.abandon(dto);
        return success();
    }

    @GetMapping("/getActive")
    public ResponseVO<RecordActiveVO> getActive() {
        return success(exerciseRecordWebBiz.getActive());
    }

    @GetMapping("/listToday")
    public ResponseVO<List<ExerciseRecordVO>> listToday() {
        return success(exerciseRecordWebBiz.listToday());
    }

    @GetMapping("/sumTodayActual")
    public ResponseVO<Integer> sumTodayActual() {
        return success(exerciseRecordWebBiz.sumTodayActual());
    }
}