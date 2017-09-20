package com.dili.ss.domain;

/**
 * 用户和表格列关系,用于向用户展示过滤后的数据列
 * Created by asiamaster on 2017/9/18 0018.
 */
public class UserColumn {
	//用户id
	private Long userId;

	//系统名(或标识)
	private String system;

	//模块名(或标识)
	private String module;

	//命名空间(用于区分一个功能模块下有多个表格,默认为"default")
	private String namespace = "default";

	//列名
	private String[] columns;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}
}
