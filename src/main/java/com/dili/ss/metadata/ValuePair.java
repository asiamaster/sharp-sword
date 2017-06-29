package com.dili.ss.metadata;

import java.io.Serializable;

/**
 * 值对类
 * @author WangMi
 * @create 2008-11-7
 */
public interface ValuePair<T> extends Serializable {
	/**
	 * 取值对的名称
	 *
	 * @return
	 */
	public String getText();

	/**
	 * 设置值对的名称
	 *
	 * @param name
	 */
	public void setText(String name);

	/**
	 * 取值对的值
	 *
	 * @return
	 */
	public T getValue();

	/**
	 * 设置值对的值
	 *
	 * @param value
	 * @return
	 */
	public void setValue(T value);

}

