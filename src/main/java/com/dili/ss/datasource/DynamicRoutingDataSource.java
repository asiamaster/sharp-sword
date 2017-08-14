package com.dili.ss.datasource;

import com.dili.ss.datasource.aop.DynamicRoutingDataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 多数据源模式，动态路由数据源
 * Created by asiamaster on 2017/8/8 0008.
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicRoutingDataSourceContextHolder.peek();
	}

}
