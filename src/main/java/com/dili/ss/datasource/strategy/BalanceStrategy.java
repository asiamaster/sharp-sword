package com.dili.ss.datasource.strategy;

/**
 * balance strategy, fetch index from the list
 * 
 * @author asiamastor
 *
 */
public abstract class BalanceStrategy {

	/**
	 * fetch next index
	 * 
	 * @return
	 */
	public abstract int next();
}
