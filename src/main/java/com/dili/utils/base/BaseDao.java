package com.dili.utils.base;


import com.dili.utils.domain.BaseQuery;

import java.io.Serializable;
import java.util.List;

/**
 * baseDao
 * @param <T>
 * @param <KEY>
 */
public interface BaseDao<T,KEY extends Serializable> {

	/**
	 * 添加对象
	 * @param t
	 * @return 影响条数
	 */
	int insert(T t);
	
	/**
	 * 添加对象
	 * @param list
	 * @return 影响条数
	 */
	int batchInsert(List<T> list);
	
	/**
	 * 删除对象,主键
	 * @param key
	 * @return 影响条数
	 */
	int del(KEY... key);

	/**
	 * 更新对象,条件主键ID
	 * @param t
	 * @return 影响条数
	 */
	int update(T t);
	
	/**
	 * 更新对象,条件主键ID
	 * @param list
	 * @return 影响条数
	 */
	int batchUpdate(List<T> list);

	/**
	 * 查询对象,条件主键
	 * @param key
	 * @return
	 */
	T get(KEY key);
	
	/**
	 * 查询对象,条件主键数组
	 * @param key
	 * @return
	 */
	List<T> list(KEY key);

	/**
	 * 查询对象,只要不为NULL与空则为条件
	 * @param condtion
	 * @return
	 */
	List<T> list(BaseQuery condtion);

	/**
	 * 查询对象总数
	 * @param condtion
	 * @return
	 */
	Integer listCount(BaseQuery condtion);
}
