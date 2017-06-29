package com.dili.ss.servlet;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by asiamaster on 2017/6/19 0019.
 */
public class CSRFTokenUtil {
	public static String generate(HttpServletRequest request) {
		return UUID.randomUUID().toString();
	}
}
