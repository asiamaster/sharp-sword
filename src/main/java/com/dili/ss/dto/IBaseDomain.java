package com.dili.ss.dto;

import javax.persistence.Transient;
import java.util.Map;


/**
 * 基础实体类
 */
public interface IBaseDomain extends IDTO {

	public final static String ID = "id";
	@Transient
	public Long getId();
	public void setId(Long id);

	@Transient
	public Integer getPage();
	public void setPage(Integer page);

	@Transient
	public Integer getRows();
	public void setRows(Integer rows);

	@Transient
	public String getSort();
	public void setSort(String sort);

	@Transient
	public String getOrder();
	public void setOrder(String order);

	@Transient
	public Object getMetadata(String key);
	public void setMetadata(String key, Object value);

	@Transient
	public Map getMetadata();
	public void setMetadata(Map metadata);

	public Boolean containsMetadata(String key);
}
