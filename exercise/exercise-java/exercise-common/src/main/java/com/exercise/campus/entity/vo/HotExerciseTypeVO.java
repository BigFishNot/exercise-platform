package com.exercise.campus.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 热门运动类型 VO
 */
@Data
public class HotExerciseTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String typeId;
    private String typeName;
    private String typeIcon;
    private Long recordCount;
    private Long totalSeconds;
}