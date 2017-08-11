package com.dili.ss.datasource.strategy;

import java.util.concurrent.atomic.AtomicLong;

/**
 * round robin strategy, absolutely fair to reach each number which less than
 * the {@link #length}
 * 
 * @author asiamastor
 *
 */
public class RoundRobinStrategy extends BalanceStrategy {

	/**
	 * length
	 */
	private int length;

	/**
	 * index, use {@link AtomicLong} to avoid lock. in this situation, long is
	 * safe even not to reset to 0 when reach to the value of length.
	 */
	private static AtomicLong index = new AtomicLong(0);

	public RoundRobinStrategy(int length) {
		this.length = length;
	}

	@Override
	public int next() {
		return (int) index.getAndIncrement() % length;
	}

}
