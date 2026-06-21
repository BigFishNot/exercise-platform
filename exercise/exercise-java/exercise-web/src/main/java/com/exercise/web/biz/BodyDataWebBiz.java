package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.entity.dto.BodyDataAddDTO;
import com.exercise.campus.entity.query.BodyDataTrendQuery;
import com.exercise.campus.entity.vo.BodyDataSummaryVO;
import com.exercise.campus.entity.vo.BodyDataTrendVO;
import com.exercise.campus.entity.vo.BodyDataVO;
import com.exercise.campus.service.BodyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 身体数据 Web Biz
 */
@Component
public class BodyDataWebBiz {

    @Autowired
    private BodyDataService bodyDataService;

    public Long upsert(BodyDataAddDTO dto) {
        String userId = LoginContextHolder.requireUserId();
        return bodyDataService.upsert(userId, dto);
    }

    public BodyDataVO getToday() {
        String userId = LoginContextHolder.requireUserId();
        return bodyDataService.getToday(userId);
    }

    public BodyDataVO getById(Long id) {
        return bodyDataService.getById(id);
    }

    public void delete(Long id) {
        bodyDataService.delete(id);
    }

    public TrendVO getTrend(BodyDataTrendQuery query) {
        String userId = LoginContextHolder.requireUserId();
        BodyDataService.TrendResult r = bodyDataService.getTrend(userId, query);
        return new TrendVO(r.summary(), r.points());
    }

    /** 趋势返回结构（前端不直接用 Java record） */
    public record TrendVO(BodyDataSummaryVO summary, List<BodyDataTrendVO> points) {}
}