package com.dili.ss.dto;


import com.dili.ss.exception.InternalException;

/**
 * DTOData代理异常
 * @author WangMi
 * Created by asiamaster on 2017/7/31 0031.
 */
public class DTOProxyException extends InternalException {
	private static final long serialVersionUID = -88899901616112234L;

	/**
	 * @param message
	 */
	public DTOProxyException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DTOProxyException(String message, Throwable cause) {
		super(message, cause);
	}

}
