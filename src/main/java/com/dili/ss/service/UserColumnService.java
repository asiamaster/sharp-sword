package com.dili.ss.service;

import com.dili.ss.domain.UserColumn;

/**
 * 前端界面服务
 * Created by asiamaster on 2017/9/18 0018.
 */
public interface UserColumnService {

	/**
	 * 保存用户和表格列关系(到redis),用于向用户展示过滤后的数据列
	 * @param userColumn
	 * @return
	 */
	void saveUserColumns(UserColumn userColumn);

	/**
	 * 根据userId, system, module和namespace获取用户和表格列关系(from redis),
	 * 用于向用户展示过滤后的数据列
	 * @param userColumn
	 * @return
	 */
	String[] getUserColumns(UserColumn userColumn);
}
