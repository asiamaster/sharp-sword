package com.dili.ss.datasource.aop;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 动态数据源切换上下文持有者
 * Created by asiamaster on 2017/8/8 0008.
 */
public class DynamicRoutingDataSourceContextHolder {

	//先进后出(FILO)栈
	private static final ThreadLocal<Stack<String>> contextHolder = new ThreadLocal<Stack<String>>();

	//仅用于判断是否存在数据源
	public static List<String> dataSourceIds = new ArrayList<>();

	public static void setDataSourceType(Stack<String> dataSourceType) {
		contextHolder.set(dataSourceType);
	}

	public static Stack<String> getDataSourceType() {
		if(contextHolder.get() == null){
			contextHolder.set(new Stack<String>());
		}
		return contextHolder.get();
	}

	/**
	 * 获取当前数据源，数据不变
	 * @return
	 */
	public static String peek() {
		if(contextHolder.get() == null || contextHolder.get().isEmpty()){
			return null;
		}
		return contextHolder.get().peek();
	}

	/**
	 * 栈顶推进一个数据源
	 * @return
	 */
	public static String push(String value) {
		if(contextHolder.get() == null){
			contextHolder.set(new Stack<String>());
		}
		return contextHolder.get().push(value);
	}

	/**
	 * 栈顶弹出一个数据源, 没数据返回null，不会抛异常
	 * @return
	 */
	public static String pop() {
		if(contextHolder.get() == null || contextHolder.get().isEmpty()){
			return null;
		}
		return contextHolder.get().pop();
	}

	public static void clear() {
		contextHolder.remove();
	}

	/**
	 * 判断指定DataSrouce当前是否存在
	 *
	 * @param dataSourceId
	 * @return
	 */
	public static boolean containsDataSource(String dataSourceId){
		return dataSourceIds.contains(dataSourceId);
	}

}
