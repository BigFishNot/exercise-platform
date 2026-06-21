package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 身体数据趋势 VO（曲线点）
 */
@Data
public class BodyDataTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date recordDate;
    private BigDecimal weight;
    private BigDecimal bmi;
}