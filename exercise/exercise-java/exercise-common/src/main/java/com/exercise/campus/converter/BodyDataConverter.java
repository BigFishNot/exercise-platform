package com.exercise.campus.converter;

import com.exercise.campus.entity.dto.BodyDataAddDTO;
import com.exercise.campus.entity.po.BodyData;
import com.exercise.campus.entity.vo.BodyDataSummaryVO;
import com.exercise.campus.entity.vo.BodyDataTrendVO;
import com.exercise.campus.entity.vo.BodyDataVO;
import com.exercise.campus.enums.BodyDataSourceEnum;
import com.exercise.campus.utils.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 身体数据 Converter
 */
public class BodyDataConverter {

    public static BodyData toPO(BodyDataAddDTO dto, String userId) {
        BodyData po = new BodyData();
        po.setUserId(userId);
        po.setRecordDate(dto.getRecordDate() != null ? dto.getRecordDate() : DateUtils.todayGMT8());
        po.setWeight(dto.getWeight());
        po.setBmi(BigDecimal.ZERO); // 后面 service 计算后回填
        po.setSource(BodyDataSourceEnum.MANUAL.getSource());
        po.setRemark(dto.getRemark());
        return po;
    }

    public static BodyDataVO toVO(BodyData po) {
        if (po == null) return null;
        BodyDataVO vo = new BodyDataVO();
        vo.setId(po.getId());
        vo.setUserId(po.getUserId());
        vo.setRecordDate(po.getRecordDate());
        vo.setWeight(po.getWeight());
        vo.setBmi(po.getBmi());
        vo.setSource(po.getSource());
        vo.setSourceName(po.getSource() != null
                ? BodyDataSourceEnum.of(po.getSource()).getDesc() : null);
        vo.setRemark(po.getRemark());
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }

    public static BodyDataTrendVO toTrendVO(BodyData po) {
        if (po == null) return null;
        BodyDataTrendVO vo = new BodyDataTrendVO();
        vo.setRecordDate(po.getRecordDate());
        vo.setWeight(po.getWeight());
        vo.setBmi(po.getBmi());
        return vo;
    }

    public static List<BodyDataTrendVO> toTrendVOList(List<BodyData> pos) {
        if (pos == null || pos.isEmpty()) return Collections.emptyList();
        return pos.stream().map(BodyDataConverter::toTrendVO).collect(Collectors.toList());
    }

    public static BigDecimal computeBmi(BigDecimal weightKg, BigDecimal heightCm) {
        if (weightKg == null || heightCm == null || heightCm.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // BMI = weight / (height/100)^2
        BigDecimal heightM = heightCm.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal heightM2 = heightM.multiply(heightM);
        return weightKg.divide(heightM2, 2, RoundingMode.HALF_UP);
    }

    public static BodyDataSummaryVO toSummaryVO(List<BodyData> pos, BigDecimal heightCm) {
        BodyDataSummaryVO vo = new BodyDataSummaryVO();
        if (pos == null || pos.isEmpty()) {
            vo.setRecordDays(0);
            return vo;
        }
        vo.setRecordDays(pos.size());

        BigDecimal min = null, max = null, sum = BigDecimal.ZERO;
        for (BodyData p : pos) {
            BigDecimal w = p.getWeight();
            if (w == null) continue;
            if (min == null || w.compareTo(min) < 0) min = w;
            if (max == null || w.compareTo(max) > 0) max = w;
            sum = sum.add(w);
        }
        vo.setMinWeight(min);
        vo.setMaxWeight(max);
        vo.setAvgWeight(pos.isEmpty() ? BigDecimal.ZERO
                : sum.divide(BigDecimal.valueOf(pos.size()), 2, RoundingMode.HALF_UP));

        // 最新一条 = 列表最后（按日期升序）
        BodyData latest = pos.get(pos.size() - 1);
        vo.setLatestWeight(latest.getWeight());
        vo.setLatestBmi(latest.getBmi() != null ? latest.getBmi()
                : computeBmi(latest.getWeight(), heightCm));

        // 7 天前那条
        if (pos.size() >= 2) {
            int idx7dAgo = Math.max(0, pos.size() - 1 - 7);
            BodyData old = pos.get(idx7dAgo);
            if (old.getWeight() != null && latest.getWeight() != null) {
                vo.setWeightChange7d(latest.getWeight().subtract(old.getWeight())
                        .setScale(2, RoundingMode.HALF_UP));
            }
        }
        return vo;
    }
}