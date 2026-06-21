package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 身体数据 - 管理端按用户查参数
 */
@Data
public class BodyDataAdminQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private Integer limit;
}