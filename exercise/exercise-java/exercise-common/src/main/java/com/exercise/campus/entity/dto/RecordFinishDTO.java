package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 完成运动 DTO
 */
@Data
public class RecordFinishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recordId;
    /** 实际秒数（前端累计的真实耗时） */
    private Integer actualSeconds;
}