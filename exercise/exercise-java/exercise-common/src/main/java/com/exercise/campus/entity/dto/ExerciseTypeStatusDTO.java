package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 运动类型启停 DTO
 * 仅承载 typeId 与 status 两个字段，与 update 解耦
 */
@Data
public class ExerciseTypeStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String typeId;
    private Integer status;
}