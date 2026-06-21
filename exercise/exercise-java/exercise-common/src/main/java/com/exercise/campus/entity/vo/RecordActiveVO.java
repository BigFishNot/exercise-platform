package com.exercise.campus.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 当前进行中的运动记录（用于前端恢复倒计时）
 */
@Data
public class RecordActiveVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recordId;
    private String typeId;
    private String typeName;
    private String typeIcon;
    private Integer planSeconds;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
}