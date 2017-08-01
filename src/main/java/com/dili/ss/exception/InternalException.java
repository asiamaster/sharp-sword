package com.dili.ss.exception;

/**
 * 内部异常
 *
 * @author WangMi
 * Created by asiamaster on 2017/7/31 0031.
 */
public class InternalException extends RuntimeException {
	private static final long serialVersionUID = -613311234553268165L;
	private static final String DEFAULT_MESS = "程序内部错误!";

	public InternalException(String message) {
		super(message);
	}

	public InternalException(Throwable cause) {
		super(DEFAULT_MESS, cause);
	}

	public InternalException(String message, Throwable cause) {
		super(message, cause);
	}
}
