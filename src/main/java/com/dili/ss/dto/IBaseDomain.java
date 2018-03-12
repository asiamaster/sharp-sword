package com.dili.ss.dto;

import javax.persistence.Transient;
import java.util.Map;


/**
 * 基础实体类
 */
public interface IBaseDomain extends IDTO {

	String ID = "id";
	@Transient
	Long getId();
	void setId(Long id);

	@Transient
	Integer getPage();
	void setPage(Integer page);

	@Transient
	Integer getRows();
	void setRows(Integer rows);

	@Transient
	String getSort();
	void setSort(String sort);

	@Transient
	String getOrder();
	void setOrder(String order);

	@Transient
	Object getMetadata(String key);
	void setMetadata(String key, Object value);

	@Transient
	Map getMetadata();
	void setMetadata(Map metadata);

	Boolean containsMetadata(String key);
}
