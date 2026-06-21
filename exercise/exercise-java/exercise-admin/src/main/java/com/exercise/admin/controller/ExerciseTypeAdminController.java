package com.exercise.admin.controller;

import com.exercise.admin.biz.ExerciseTypeAdminBiz;
import com.exercise.campus.controller.BaseController;
import com.exercise.campus.entity.dto.ExerciseTypeAddDTO;
import com.exercise.campus.entity.dto.ExerciseTypeStatusDTO;
import com.exercise.campus.entity.dto.ExerciseTypeUpdateDTO;
import com.exercise.campus.entity.query.ExerciseTypeQuery;
import com.exercise.campus.entity.vo.ExerciseTypeAdminVO;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.campus.vo.ResponseVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运动类型 Admin Controller
 * 路径：/api/exerciseType/*
 */
@RestController
@RequestMapping("/exerciseType")
@Validated
public class ExerciseTypeAdminController extends BaseController {

    @Autowired
    private ExerciseTypeAdminBiz exerciseTypeAdminBiz;

    @PostMapping("/loadDataList")
    public ResponseVO<PageResultVO<ExerciseTypeAdminVO>> loadDataList(@RequestBody ExerciseTypeQuery query) {
        return success(exerciseTypeAdminBiz.loadDataList(query));
    }

    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody @Valid ExerciseTypeAddDTO dto) {
        exerciseTypeAdminBiz.add(dto);
        return success();
    }

    @PostMapping("/update")
    public ResponseVO<Void> update(@RequestBody @Valid ExerciseTypeUpdateDTO dto) {
        exerciseTypeAdminBiz.update(dto);
        return success();
    }

    @PostMapping("/delete")
    public ResponseVO<Void> delete(@RequestBody ExerciseTypeUpdateDTO dto) {
        exerciseTypeAdminBiz.delete(dto.getTypeId());
        return success();
    }

    @PostMapping("/updateStatus")
    public ResponseVO<Void> updateStatus(@RequestBody ExerciseTypeStatusDTO dto) {
        exerciseTypeAdminBiz.updateStatus(dto.getTypeId(), dto.getStatus());
        return success();
    }
}