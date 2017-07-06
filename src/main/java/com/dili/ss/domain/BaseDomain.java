package com.dili.ss.domain;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * 基础实体类
 */
public class BaseDomain implements Serializable {
	/** id */
//	@Id
//	@Column(name = "id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Transient
	protected Long id;
	@Transient
	private Integer page;	//页码，从1开始
	@Transient
	private Integer rows; //每页行数
//	@Transient
//	private String sort;    //排序字段，以逗号分隔
//	@Transient
//	private String order;   //排序类型: asc,desc
	@Transient
	private Map metadata;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

//	public String getSort() {
//		return sort;
//	}
//
//	public void setSort(String sort) {
//		this.sort = sort;
//	}
//
//	public String getOrder() {
//		return order;
//	}
//
//	public void setOrder(String order) {
//		this.order = order;
//	}

	public void mset(String key, Object value){
		if(metadata == null){
			metadata = new HashMap();
		}
		metadata.put(key, value);
	}

	public Object mget(String key){
		return metadata == null ? null : metadata.get(key);
	}

	public Map getMetadata() {
		return metadata;
	}

	public void setMetadata(Map metadata) {
		this.metadata = metadata;
	}
}
