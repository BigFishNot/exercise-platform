package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 阶段计划取消 DTO（仅需 planId）
 */
@Data
public class PlanIdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planId;
}