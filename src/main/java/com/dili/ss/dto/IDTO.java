package com.dili.ss.dto;

/**
 * Created by asiamaster on 2017/7/31 0031.
 */
public interface IDTO {
	/**
	 * 代理取值
	 *
	 * @param property
	 * @return
	 */
	public Object aget(String property);

	/**
	 * 代理设置
	 *
	 * @param property
	 * @param value
	 */
	public void aset(String property, Object value);
}
