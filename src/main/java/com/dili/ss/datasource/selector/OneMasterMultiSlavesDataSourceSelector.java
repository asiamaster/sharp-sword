package com.dili.ss.datasource.selector;


import com.dili.ss.datasource.DataSourceManager;

/**
 * one master multi slaves model, the target master will be returned if current
 * operation is writable
 * 
 * @author asiamastor
 *
 */
public abstract class OneMasterMultiSlavesDataSourceSelector extends DataSourceSelector {

	/**
	 * if current operation is writable, the target data source will be returned
	 * regardless if it is failed or not, because only one master is available.
	 * 
	 * if it's read only, the failed data sources will be ignored
	 */
	@Override
	public String fetch(boolean writable) {
		return writable ? DataSourceManager.master : fetchSlave();
	}

	@Override
	public String fetchDefault() {
		return DataSourceManager.getDefault();
	}

	/**
	 * fetch slave exclude failed
	 * 
	 * @return
	 */
	protected abstract String fetchSlave();

}
