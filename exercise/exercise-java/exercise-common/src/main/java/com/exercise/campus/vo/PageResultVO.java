package com.exercise.campus.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果 VO
 * 前后端分页字段固定：total / pageNum / pageSize / list
 */
@Data
public class PageResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;

    public PageResultVO() {
    }

    public PageResultVO(Long total, Integer pageNum, Integer pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list == null ? Collections.emptyList() : list;
    }

    /**
     * 由 PageHelper 的 PageInfo 转统一分页结果
     */
    public static <T> PageResultVO<T> of(PageInfo<T> pageInfo) {
        if (pageInfo == null) {
            return new PageResultVO<>(0L, 1, 0, Collections.emptyList());
        }
        return new PageResultVO<>(pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getList());
    }

    public static <T> PageResultVO<T> empty() {
        return new PageResultVO<>(0L, 1, 0, Collections.emptyList());
    }
}