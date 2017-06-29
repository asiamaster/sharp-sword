/*
 * Copyright (c) 2014 www.diligrp.com All rights reserved.
 * 本软件源代码版权归----所有,未经许可不得任意复制与传播.
 */
package com.dili.ss.domain;


import com.dili.ss.constant.ResultCode;

/**
 * 基础输出对象
 * 
 * @author dev-center
 * @since 2014-05-10
 */
public class BaseOutput<T> {

    /**
     * 业务状态码 <br/>
     * 200代表成功，其它表示失败，具体失败原因请查看ResultCode属性
     */
    private String code;//
    /**
     * 业务状态说明 <br/>
     * 200时返回OK，code!=200时表示具体失败原因
     */
    private String result;
    /**
     * 返回业务数据 <br/>
     * 根据接口泛型指定
     */
    private T data;// 数据
    
    /**
     * 非业务数据，api调用业务状态码不为200时的错误数据，具体数据是否有值，查看ResultCode,根据状态码确定
     */
    private String errorData;

    public BaseOutput() {
    }

    public BaseOutput(String code, String result) {
        this.code = code;
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public BaseOutput setCode(String code) {
        this.code = code;
        return this;
    }

    public String getResult() {
        return result;
    }

    public BaseOutput setResult(String result) {
        this.result = result;
        return this;
    }

    public T getData() {
        return (T) data;
    }

    public BaseOutput setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> BaseOutput<T> create(String code, String result) {
        return new BaseOutput<T>(code, result);
    }

    public static <T> BaseOutput<T> success() {
        return success("OK");
    }

    public static <T> BaseOutput<T> success(String msg) {
        return create(ResultCode.OK, msg);
    }

    public static <T> BaseOutput<T> failure() {
        return failure("操作失败!");
    }

    public static <T> BaseOutput<T> failure(String msg) {
        return create(ResultCode.APP_ERROR, msg);
    }

    
    public String getErrorData() {
        return errorData;
    }
    
    public BaseOutput setErrorData(String errorData) {
        this.errorData = errorData;
        return this;
    }

    public boolean isSuccess(){
        return ResultCode.OK.equals(this.code);
    }
}
