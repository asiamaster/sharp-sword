package com.dili.ss.datasource.aop;

import java.lang.reflect.Method;

/**
 * 主从数据库上下文持有者
 * @author asiamastor
 */
public class MasterSlaveContextHolder {

	private static ThreadLocal<AccessType> accessType = new ThreadLocal<AccessType>();
	private static ThreadLocal<Method> stackTopMethod = new ThreadLocal<Method>();

	public static void read() {
		accessType.set(AccessType.READ);
	}

	public static void write() {
		accessType.set(AccessType.WRITE);
	}
	
	public static void clean() {
		accessType.remove();
		stackTopMethod.remove();
	}

	public static boolean writable() {
		return AccessType.WRITE.equals(accessType.get());
	}

	public static boolean isAvaiable() {
		return null != accessType.get();
	}

	/**
	 * stackTopMethod为空时，推进外层方法
	 * 
	 * @param method
	 */
	public static void pushOuterMethod(Method method) {
		if (null == stackTopMethod.get())
			stackTopMethod.set(method);
	}
	
	/**
	 * 强制推进外层方法
	 * 
	 * @param method
	 */
	public static void pushOuterMethodByForce(Method method) {
		stackTopMethod.set(method);
	}
	
	public static Method getStackTopMethod(){
		return stackTopMethod.get();
	}

	/**
	 * 拉取外层方法
	 * 
	 * @param method
	 */
	public static void pullOutMethod(Method method) {
		if (stackTopMethod.get() == method) {
			stackTopMethod.set(null);
			accessType.set(null);
		}
	}

	private enum AccessType {
		READ, WRITE
	}

}
