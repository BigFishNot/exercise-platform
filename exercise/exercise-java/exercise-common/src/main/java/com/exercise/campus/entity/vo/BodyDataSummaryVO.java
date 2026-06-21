package com.exercise.campus.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 身体数据汇总 VO（趋势接口顶部展示用）
 */
@Data
public class BodyDataSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 最新一条体重 */
    private BigDecimal latestWeight;
    /** 最新一条 BMI */
    private BigDecimal latestBmi;
    /** 最近 7 天变化（最新 - 7 天前），正数=上升，负数=下降 */
    private BigDecimal weightChange7d;
    /** 区间最低体重 */
    private BigDecimal minWeight;
    /** 区间最高体重 */
    private BigDecimal maxWeight;
    /** 区间平均体重 */
    private BigDecimal avgWeight;
    /** 区间记录天数 */
    private Integer recordDays;
}