package com.exercise.campus.service;

import com.exercise.campus.entity.dto.BodyDataAddDTO;
import com.exercise.campus.entity.po.BodyData;
import com.exercise.campus.entity.query.BodyDataTrendQuery;
import com.exercise.campus.entity.vo.BodyDataSummaryVO;
import com.exercise.campus.entity.vo.BodyDataTrendVO;
import com.exercise.campus.entity.vo.BodyDataVO;

import java.util.List;

/**
 * 身体数据 Service
 */
public interface BodyDataService {

    /** 新增/更新（按 userId + recordDate 幂等） */
    Long upsert(String userId, BodyDataAddDTO dto);

    /** 当日记录（无则 null） */
    BodyDataVO getToday(String userId);

    /** 单条详情 */
    BodyDataVO getById(Long id);

    /** 趋势查询（含汇总） */
    TrendResult getTrend(String userId, BodyDataTrendQuery query);

    /** 单条删除 */
    void delete(Long id);

    /** 内部用：拉 PO（无记录返回 null） */
    BodyData loadTodayPO(String userId);

    /** 趋势返回结构 */
    record TrendResult(BodyDataSummaryVO summary, List<BodyDataTrendVO> points) {}
}