package com.exercise.campus.converter;

import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.vo.UserInfoVO;
import com.exercise.campus.enums.GenderEnum;
import com.exercise.campus.enums.RoleTypeEnum;
import com.exercise.campus.enums.StatusEnum;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * UserInfo PO → VO 转换器
 */
public class UserInfoConverter {

    private UserInfoConverter() {
    }

    public static UserInfoVO toVO(UserInfo po) {
        if (po == null) {
            return null;
        }
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(po, vo);

        GenderEnum g = GenderEnum.of(po.getGender());
        vo.setGenderName(g == null ? "" : g.getDesc());

        RoleTypeEnum r = RoleTypeEnum.of(po.getRoleType());
        vo.setRoleTypeName(r == null ? "" : r.getDesc());

        StatusEnum s = StatusEnum.of(po.getStatus());
        vo.setStatusName(s == null ? "" : s.getDesc());

        // BMI = weight(kg) / (height(m))^2
        if (po.getWeight() != null && po.getHeight() != null
                && po.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightMeter = po.getHeight().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal bmi = po.getWeight().divide(heightMeter.multiply(heightMeter), 2, RoundingMode.HALF_UP);
            vo.setBmi(bmi);
        }
        return vo;
    }
}