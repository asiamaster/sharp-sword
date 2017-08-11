package com.dili.ss.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主从数据源管理器
 * Created by asiamaster on 2017/8/10 0010.
 */
public class DataSourceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceManager.class);

	//数据源切换模式(主从和多数据源)
	public static SwitchMode switchMode;

	//负载均衡器选择模式
	public static SelectorMode selectorMode;
	/**
	 * 主数据源名称，默认为datasource
	 */
	public static String master = SwitchDataSource.DEFAULT_DATASOURCE;

	/**
	 * 从数据源名称列表
	 */
	public static List<String> slaves = new ArrayList<>(4);

	/**
	 * 从数据源权重
	 */
	public static Map<String, Integer> weights = new HashMap<>();

	/**
	 * 获取默认的(第一个)从数据源
	 *
	 * @return
	 */
	public static String getDefault() {
		return slaves.get(0);
	}

	public static String fetchSlave(int index) {
		return slaves.get(index);
	}

}
