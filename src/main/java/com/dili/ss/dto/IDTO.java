package com.dili.ss.dto;

import com.dili.ss.util.POJOUtils;
import org.apache.commons.collections.map.HashedMap;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by asiamaster on 2017/7/31 0031.
 */
public interface IDTO extends Serializable {
	/**
	 * 代理取值
	 *
	 * @param property
	 * @return
	 */
	public default Object aget(String property){
		return POJOUtils.getProperty(this, property);
	};

	/**
	 * 代理设置
	 *
	 * @param property
	 * @param value
	 */
	public default void aset(String property, Object value){
		POJOUtils.setProperty(this, property, value);
	};

	/**
	 * meta取值
	 * @param key
	 * @return
	 */
	public default Object mget(String key){return null;};

	/**
	 * meta取所有值
	 * @return
	 */
	public default Map<String, Object> mget(){return new HashedMap();};

	/**
	 * meta设值
	 * @param key
	 * @param value
	 */
	public default void mset(String key, Object value){};

	/**
	 * meta设值
	 * @return
	 */
	public default void mset(Map<String, Object> metadata){};
}
