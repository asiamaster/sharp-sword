/*
 * Copyright (c) 2014 www.diligrp.com All rights reserved.
 * 本软件源代码版权归----所有,未经许可不得任意复制与传播.
 */
package com.dili.ss.exception;

/**
 * AppException
 * @author dev-center
 * @since 2014-05-15
 */
public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public static final String CODE_NEGLECTABLE = "201";
	private String code;
	private String errorData;
	public AppException() {
		super();
	}
	
	public AppException(String message) {
		super(message);
	}
	
	public AppException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(String code, String message) {
	    super(message);
	    this.code=code;
    }
	
	public AppException(String code, String errorData, String message) {
        super(message);
        this.code=code;
        this.errorData=errorData;
    }
	
	
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    
    public String getErrorData() {
        return errorData;
    }

    
    public void setErrorData(String errorData) {
        this.errorData = errorData;
    }

    @Override
    public String toString() {
        return "AppException [code=" + getCode() + ", errorData="
                + getErrorData() + ", message=" + getMessage()
                + ", cause=" + getCause() + "]";
    }
}
