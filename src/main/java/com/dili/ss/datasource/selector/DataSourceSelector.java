package com.dili.ss.datasource.selector;


/**
 * data source selector
 * @author asiamastor
 */
public abstract class DataSourceSelector {

	/**
	 * fetch data source with writable flag and current failed data source name
	 * 
	 * @param writable
	 * @return
	 */
	public abstract String fetch(boolean writable);

	/**
	 * get default data source
	 * 
	 * @return
	 */
	public abstract String fetchDefault();

}
