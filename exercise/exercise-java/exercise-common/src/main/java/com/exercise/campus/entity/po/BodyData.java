package com.exercise.campus.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 身体数据 PO
 */
@Data
public class BodyData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date recordDate;

    private BigDecimal weight;
    private BigDecimal bmi;
    private Integer source;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}