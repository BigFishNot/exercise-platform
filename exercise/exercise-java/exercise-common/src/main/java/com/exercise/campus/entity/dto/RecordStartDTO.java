package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 开始运动 DTO
 */
@Data
public class RecordStartDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String typeId;
    /** 计划秒数（10-7200） */
    private Integer planSeconds;
    private String remark;
}