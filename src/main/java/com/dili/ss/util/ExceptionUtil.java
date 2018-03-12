package com.dili.ss.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 异常处理工具类
 * Created by asiamaster on 2017/12/4 0004.
 */
public class ExceptionUtil {
	/**
	 * 创建异常堆栈信息
	 * @param message
	 * @param cause
	 * @return
	 */
	public static String buildMessage(String message, Throwable cause) {
		if (cause != null) {
			StringBuilder buf = new StringBuilder();
			if (message != null) {
				buf.append(message).append("; ");
			}
			buf.append("nested exception is ").append(cause);
			return buf.toString();
		}
		else {
			return message;
		}
	}

	/**
	 * 获取异常信息内容
	 * @param e 异常对象
	 * @param length 指定长度,0表示不做截取
	 * @return 返回异常信息内容
	 */
	public static String getExceptionString(Throwable e, Integer length) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		e.printStackTrace(ps);
		String msg = os.toString();
		if (length != 0 && msg.length() > length) {
			msg=msg.substring(0, length);
		}
		return msg;
	}

	public static String getExceptionString(Exception e, Integer length) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		e.printStackTrace(ps);
		String msg = os.toString();
		if (length != 0 && msg.length() > length) {
			msg=msg.substring(0, length);
		}
		return msg;
	}
}
