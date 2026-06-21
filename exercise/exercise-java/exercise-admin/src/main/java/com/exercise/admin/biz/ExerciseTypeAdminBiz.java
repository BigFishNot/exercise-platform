package com.exercise.admin.biz;

import com.exercise.campus.entity.dto.ExerciseTypeAddDTO;
import com.exercise.campus.entity.dto.ExerciseTypeUpdateDTO;
import com.exercise.campus.entity.query.ExerciseTypeQuery;
import com.exercise.campus.entity.vo.ExerciseTypeAdminVO;
import com.exercise.campus.service.ExerciseTypeService;
import com.exercise.campus.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 运动类型 Admin Biz
 */
@Component
public class ExerciseTypeAdminBiz {

    @Autowired
    private ExerciseTypeService exerciseTypeService;

    public PageResultVO<ExerciseTypeAdminVO> loadDataList(ExerciseTypeQuery query) {
        return exerciseTypeService.pageList(query);
    }

    public void add(ExerciseTypeAddDTO dto) {
        exerciseTypeService.add(dto);
    }

    public void update(ExerciseTypeUpdateDTO dto) {
        exerciseTypeService.update(dto);
    }

    public void delete(String typeId) {
        exerciseTypeService.delete(typeId);
    }

    public void updateStatus(String typeId, Integer status) {
        exerciseTypeService.updateStatus(typeId, status);
    }
}