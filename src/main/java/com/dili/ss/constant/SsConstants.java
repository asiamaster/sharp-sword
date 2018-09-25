package com.dili.ss.constant;

import com.dili.ss.util.SystemConfigUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asiamaster on 2017/7/25 0025.
 */
public class SsConstants {
	// ===================  beetl常量  ===================
	// 冒号编码，用于动态查询的参数值的冒号间隔编码
	public static final String COLON_ENCODE = "#@#@";

	public static final String ENCRYPT_PROPERTY_PASSWORD = "security";

	//导出限流数
	public static final int LIMIT = Integer.parseInt(SystemConfigUtils.getProperty("export.limit", "2"));
	//导出标识，key为js生成的唯一码， value为导出完成时间
	public static final Map<String, Long> EXPORT_FLAG = new HashMap<>(LIMIT);

}
