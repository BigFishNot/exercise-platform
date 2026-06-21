package com.exercise.campus.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 运动记录 PO
 */
@Data
public class ExerciseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recordId;
    private String userId;
    private String typeId;
    private String typeName;
    private Integer planSeconds;
    private Integer actualSeconds;
    private BigDecimal calories;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date exerciseDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private Integer status;
    private String abandonReason;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}