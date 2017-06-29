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
public class ParamErrorException extends AppException{
	private static final long serialVersionUID = 1L;
	public ParamErrorException() {
		super();
	}
	
	public ParamErrorException(String message) {
		super(message);
	}
	
	public ParamErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ParamErrorException(Throwable cause) {
		super(cause);
	}
	
    public ParamErrorException(String code, String message) {
	        super(code,message);
	}
	
    public ParamErrorException(String code, String errorData, String message) {
        super(code,errorData,message);
    }
    
    @Override
    public String toString() {
        return "ParamErrorException [code=" + getCode() + ", errorData="
                + getErrorData() + ", message=" + getMessage()
                + ", cause=" + getCause() + "]";
    }
}
