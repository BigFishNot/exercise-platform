package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用 recordId DTO（放弃 / 查询）
 */
@Data
public class RecordIdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recordId;
    private String abandonReason;
}