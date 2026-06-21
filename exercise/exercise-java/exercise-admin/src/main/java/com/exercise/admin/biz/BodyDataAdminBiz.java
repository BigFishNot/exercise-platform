package com.exercise.admin.biz;

import com.exercise.campus.entity.po.BodyData;
import com.exercise.campus.entity.vo.BodyDataVO;
import com.exercise.campus.service.BodyDataService;
import com.exercise.mappers.BodyDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 身体数据 Admin Biz
 */
@Component
public class BodyDataAdminBiz {

    @Autowired
    private BodyDataMapper bodyDataMapper;

    public List<BodyDataVO> listByUser(String userId, Integer limit) {
        if (userId == null || userId.isEmpty()) return Collections.emptyList();
        int n = limit == null || limit <= 0 ? 30 : limit;
        List<BodyData> pos = bodyDataMapper.selectRecent(userId, n);
        if (pos == null) return Collections.emptyList();
        return pos.stream()
                .map(p -> {
                    BodyDataVO vo = new BodyDataVO();
                    vo.setId(p.getId());
                    vo.setUserId(p.getUserId());
                    vo.setRecordDate(p.getRecordDate());
                    vo.setWeight(p.getWeight());
                    vo.setBmi(p.getBmi());
                    vo.setSource(p.getSource());
                    vo.setRemark(p.getRemark());
                    vo.setCreateTime(p.getCreateTime());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}