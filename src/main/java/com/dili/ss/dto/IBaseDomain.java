package com.dili.ss.dto;

import java.util.Map;


/**
 * 基础实体类
 */
public interface IBaseDomain extends IDTO {

	public final static String ID = "id";

	public Long getId();
	public void setId(Long id);

	public Integer getPage();

	public void setPage(Integer page);

	public Integer getRows();

	public void setRows(Integer rows);

	public void setMetadata(String key, Object value);

	public Object getMetadata(String key);

	public Map getMetadata();

	public void setMetadata(Map metadata);

	public Boolean containsMetadata(String key);
}
