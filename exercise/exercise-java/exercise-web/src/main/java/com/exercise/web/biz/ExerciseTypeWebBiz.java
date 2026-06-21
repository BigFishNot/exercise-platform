package com.exercise.web.biz;

import com.exercise.campus.entity.po.ExerciseType;
import com.exercise.campus.entity.vo.ExerciseTypeVO;
import com.exercise.campus.service.ExerciseTypeService;
import com.exercise.campus.converter.ExerciseTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 运动类型 Web Biz
 */
@Component
public class ExerciseTypeWebBiz {

    @Autowired
    private ExerciseTypeService exerciseTypeService;

    public List<ExerciseTypeVO> getOptions() {
        return exerciseTypeService.listEnabled();
    }

    public ExerciseTypeVO getDetail(String typeId) {
        ExerciseType po = exerciseTypeService.getById(typeId);
        return ExerciseTypeConverter.toVO(po);
    }
}