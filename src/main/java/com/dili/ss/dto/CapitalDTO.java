package com.dili.ss.dto;

/**
 * 大写属性的DTOData
 * @author wangmi
 * Created by asiamaster on 2017/8/1 0001.
 */
public class CapitalDTO extends DTO{
	private static final long serialVersionUID = -688089562635699991L;

	/**
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(((String)key).toUpperCase());
	}

	@Override
	public Object put(String key, Object value) {
		return super.put(key.toUpperCase(), value);
	}

	/**
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {
		return super.get(((String)key).toUpperCase());
	}

	@Override
	public Object remove(Object key) {
		return super.remove(((String)key).toUpperCase());
	}

}