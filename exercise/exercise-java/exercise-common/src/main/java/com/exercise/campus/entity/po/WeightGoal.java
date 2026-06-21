package com.exercise.campus.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 减肥目标 PO
 */
@Data
public class WeightGoal implements Serializable {

    private static final long serialVersionUID = 1L;

    private String goalId;
    private String userId;
    private BigDecimal targetWeight;
    private BigDecimal targetBodyFat;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date targetDate;

    private String remark;
    private Integer status;
    /** 内部用：=userId 当 ACTIVE，=NULL 归档。参与 DB 唯一约束。 */
    private String activeMarker;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date archiveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}