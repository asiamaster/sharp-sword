package com.dili.ss.domain;

/**
 * 用于动态查询控件
 * Created by asiamaster on 2017/7/24 0024.
 */
public class ConditionItems extends BaseDomain {

	private String dtoClass;
	private String conditionRelationField;
	private String[] conditionItems;

	public String getConditionRelationField() {
		return conditionRelationField;
	}

	public void setConditionRelationField(String conditionRelationField) {
		this.conditionRelationField = conditionRelationField;
	}

	public String[] getConditionItems() {
		return conditionItems;
	}

	public void setConditionItems(String[] conditionItems) {
		this.conditionItems = conditionItems;
	}

	public String getDtoClass() {
		return dtoClass;
	}

	public void setDtoClass(String dtoClass) {
		this.dtoClass = dtoClass;
	}
}
