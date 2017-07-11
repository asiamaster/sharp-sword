package com.dili.ss.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础查询类
 */
//@ApiModel(value = "baseQuery",description = "查询基础类")
public class BaseQuery implements Serializable {

	private static final String DESC ="DESC";
	private static final String ASC ="ASC";

	private static final long serialVersionUID = 1L;

//	@ApiParam(allowableValues = "DESC,ASC",value = "排序方式")
	private transient String orderFieldType;// 排序字段类型

	private transient String orderField;// 排序字段

//	@ApiModelProperty(hidden=true,value = "查询扩展,无需传入")
	private transient Map<String, Object> queryData;// 查询扩展

//	@ApiModelProperty(hidden=true)
	private transient String keyword;// 关键字查询

//	@JSONField(serialize=false)
	public String getKeyword() {
		return keyword;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderFieldType() {
		if(DESC.equalsIgnoreCase(orderFieldType) || ASC.equalsIgnoreCase(orderFieldType)) {
			return orderFieldType.toUpperCase();
		}
		return null;
	}

//	@JSONField(serialize=false)
	public String getOrderFieldNextType() {
		if(DESC.equalsIgnoreCase(orderFieldType)) {
			return DESC;
		}
		return ASC;
	}

	public void setOrderFieldType(String orderFieldType) {
		this.orderFieldType = orderFieldType;
	}

//	@JSONField(serialize=false)
	public Map<String, Object> getQueryData() {
		if(queryData != null && queryData.size() > 0) {
			return queryData;
		}
		return null;
	}

	public void setQueryData(Map<String, Object> queryData) {
		this.queryData = queryData;
	}

	//添加其它查询数据
	public void addQueryData(String key,Object value) {
		if(queryData == null) {
			queryData = new HashMap<String, Object>();
		}
		queryData.put(key, value);
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}



}
