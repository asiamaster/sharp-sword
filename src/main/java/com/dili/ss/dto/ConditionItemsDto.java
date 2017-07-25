package com.dili.ss.dto;

import com.dili.ss.domain.BaseDomain;

/**
 * Created by asiamaster on 2017/7/24 0024.
 */
public class ConditionItemsDto extends BaseDomain {

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
