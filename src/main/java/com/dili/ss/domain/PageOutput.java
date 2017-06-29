/*
 * Copyright (c) 2014 www.diligrp.com All rights reserved.
 * 本软件源代码版权归----所有,未经许可不得任意复制与传播.
 */
package com.dili.ss.domain;


import com.dili.ss.constant.ResultCode;

/**
 * 分页输出对象
 * @author wangmi
 * @since 2016-11-25
 */
public class PageOutput<T> extends BaseOutput<T> {

    /**
     * 页码，获取第page页数据
     */
    private Integer page;
    /**
     * 页大小，每页记录数
     */
    private Integer pageSize;
    /**
     * 总记录数
     */
    private Integer total;

    @Override
    public T getData() {
        return super.getData();
    }

    @Override
    public PageOutput setData(T data) {
        super.setData(data);
        return this;
    }

    /**
     * 页码，获取第page页数据
     */
    public Integer getPage() {
        return page;
    }

    public PageOutput setPage(Integer page) {
        this.page = page;
        return this;
    }

    /**
     * 页大小，每页记录数
     */
    public Integer getPageSize() {
        return pageSize;
    }

    public PageOutput setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 总记录数
     */
    public Integer getTotal() {
        return total;
    }

    public PageOutput setTotal(Integer total) {
        this.total = total;
        return this;
    }

    // -----------------------------------------------------------------------------------
    public PageOutput() {
    }

    public PageOutput(String code, String result) {
        super(code, result);
    }

    public static <T> PageOutput<T> create(String code, String result) {
        return new PageOutput<T>(code, result);
    }

    public static <T> PageOutput<T> success() {
        return success("OK");
    }

    public static <T> PageOutput<T> success(String msg) {
        return create(ResultCode.OK, msg);
    }

    public static <T> PageOutput<T> failure() {
        return failure("操作失败!");
    }

    public static <T> PageOutput<T> failure(String msg) {
        return create(ResultCode.APP_ERROR, msg);
    }

    @Override
    public String getCode() {
        return super.getCode();
    }

    @Override
    public PageOutput setCode(String code) {
        super.setCode(code);
        return this;
    }

    @Override
    public String getResult() {
        return super.getResult();
    }

    @Override
    public PageOutput setResult(String result) {
        super.setResult(result);
        return this;
    }
}
