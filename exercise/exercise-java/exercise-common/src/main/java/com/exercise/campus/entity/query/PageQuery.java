package com.exercise.campus.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基类
 * 字段固定：pageNum / pageSize / orderBy
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 默认页大小 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    private Integer pageNum = 1;
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /** 排序字段，示例："create_time desc" */
    private String orderBy;

    public Integer getPageNum() {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, 200);
    }
}