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
public class DataErrorException extends AppException{
	private static final long serialVersionUID = 1L;
	public DataErrorException() {
		super();
	}
	
	public DataErrorException(String message) {
		super(message);
	}
	
	public DataErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataErrorException(Throwable cause) {
		super(cause);
	}
	
	public DataErrorException(String code, String errorData, String message) {
        super(code,errorData,message);
    }

    @Override
    public String toString() {
        return "DataErrorException [code=" + getCode() + ", errorData="
                + getErrorData() + ", message=" + getMessage()
                + ", cause=" + getCause() + "]";
    }
}
