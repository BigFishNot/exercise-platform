package com.exercise.campus.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 身体数据新增/更新 DTO（同一天再次提交即更新）
 */
@Data
public class BodyDataAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录日期（不传则默认为今天 GMT+8） */
    private Date recordDate;
    /** 体重(kg) */
    private BigDecimal weight;
    private String remark;
}