package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 身体数据趋势查询
 * 固定范围枚举：7d / 30d / 90d / stage（用当前 plan 区间）/ custom
 */
@Data
public class BodyDataTrendQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 7d / 30d / 90d / stage / custom */
    private String range = "30d";
    /** custom 模式下的起止日期（yyyy-MM-dd） */
    private String startDate;
    private String endDate;
}