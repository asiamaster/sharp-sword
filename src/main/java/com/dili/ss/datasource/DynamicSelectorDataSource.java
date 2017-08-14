package com.dili.ss.datasource;

import com.dili.ss.datasource.aop.MasterSlaveContextHolder;
import com.dili.ss.datasource.selector.OneMasterMultiSlavesDataSourceSelector;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 主从模式，动态选择器数据源
 * Created by asiamaster on 2017/8/10
 */
public class DynamicSelectorDataSource extends AbstractRoutingDataSource {

	OneMasterMultiSlavesDataSourceSelector dataSourceSelector;

	public void setDataSourceSelector(OneMasterMultiSlavesDataSourceSelector dataSourceSelector) {
		this.dataSourceSelector = dataSourceSelector;
	}

	public OneMasterMultiSlavesDataSourceSelector getDataSourceSelector() {
		return dataSourceSelector;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return dataSourceSelector.fetch(MasterSlaveContextHolder.writable());
	}

}
