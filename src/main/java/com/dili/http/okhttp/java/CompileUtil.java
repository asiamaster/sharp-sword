package com.dili.http.okhttp.java;

import java.util.Map;

/**
 * Created by asiamaster on 2017/9/30 0030.
 */
public class CompileUtil {
	private final static JavaStringCompiler compiler;
	static {
		compiler = new JavaStringCompiler();
	}

	public static Class<?> compile(String c, String clz) throws Exception {
		String cn = clz.substring(clz.lastIndexOf(".")+1);
		Map<String, byte[]> results = compiler.compile(cn+".java", c);
		return compiler.loadClass(clz, results);
	}

}
