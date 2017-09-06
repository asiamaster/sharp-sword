package com.dili.ss.domain;

import com.dili.ss.dto.IBaseDomain;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础实体类
 */
public class BaseDomain implements IBaseDomain {

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
	//sort和order作为easyui的远程排序的关键字，同时也不建议数据库字段使用这两个词
	@Transient
	private String sort;    //排序字段，以逗号分隔
	@Transient
	private String order;   //排序类型: asc,desc
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

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setMetadata(String key, Object value){
		if(metadata == null){
			metadata = new HashMap();
		}
		metadata.put(key, value);
	}

	public Object getMetadata(String key){
		return metadata == null ? null : metadata.get(key);
	}

	public Map getMetadata() {
		return metadata;
	}

	public void setMetadata(Map metadata) {
		this.metadata = metadata;
	}

	/**
	 * 附加属性中是否存在
	 * @param key
	 * @return
	 */
	public Boolean containsMetadata(String key) {
		if(metadata != null)
			return metadata.containsKey(key);
		return false;
	}
}
