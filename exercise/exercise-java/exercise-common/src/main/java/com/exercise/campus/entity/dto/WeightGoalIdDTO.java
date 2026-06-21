package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 减肥目标 - 归档 DTO
 */
@Data
public class WeightGoalIdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String goalId;
}