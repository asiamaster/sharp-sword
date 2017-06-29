package com.dili.ss.base;

import com.dili.ss.domain.BasePage;
import com.dili.ss.domain.EasyuiPageOutput;

import java.io.Serializable;
import java.util.List;


/**
 * baseService
 * @param <T>
 * @param <KEY>
 */
public interface BaseService<T,KEY extends Serializable> {

	/**
	 * 保存一个实体，null的属性也会保存，不会使用数据库默认值
	 *
	 * @param t
	 * @return
	 */
	public int insert(T t);

	/**
	 * 保存一个实体，null的属性不会保存，会使用数据库默认值
	 *
	 * @param t
	 * @return
	 */
	public int insertSelective(T t);
	
	/**
	 * 批量插入
	 * @param list
	 * @return
	 */
	public int batchInsert(List<T> list);
	
	/**
	 * 删除对象,主键
	 * @param key 主键
	 * @return 影响条数
	 */
	public int delete(KEY key);

	/**
	 * 批量删除对象,主键集合
	 * @param keys 主键数组
	 * @return 影响条数
	 */
	public int delete(List<KEY> keys);
	
	/**
	 * 更新对象,条件主键Id
	 * @param condtion 更新对象
	 * @return 影响条数
	 */
	public int update(T condtion);

	/**
	 * 更新对象,条件主键Id，忽略空字段
	 * @param condtion 更新对象
	 * @return 影响条数
	 */
	public int updateSelective(T condtion);

	/**
	 * 批量更新，忽略空字段
	 * @param list
	 * @return
	 */
	public int batchUpdateSelective(List<T> list);

	/**
	 * 批量更新
	 * @param list
	 * @return
	 */
	public int batchUpdate(List<T> list);
	
	/**
	 * 保存或更新对象(条件主键Id)
	 * @param t 需更新的对象
	 * @return 影响条数
	 */
	public int saveOrUpdate(T t);

	/**
	 * 保存或更新对象(条件主键Id)，忽略空字段
	 * @param t 需更新的对象
	 * @return 影响条数
	 */
	public int saveOrUpdateSelective(T t);

	/**
	 * 查询对象,条件主键
	 * @param key
	 * @return 实体对象
	 */
	public T get(KEY key);

	/**
	 * 查询对象,只要不为NULL与空则为条件
	 * @param condtion 查询条件
	 * @return 对象列表
	 */
	public List<T> list(T condtion);
	
	/**
	 * 分页查询
	 * @param t 分页查询对象
	 * @return 分页对象
	 */
	public BasePage<T> listPage(T t);

	/**
	 * 根据条件查询
	 * @param example
	 * @return
	 */
	public List<T> selectByExample(Object example);

	/**
	 * 用于支持@Like, @Operator和@OrderBy 的查询
	 * @param domain
	 * @return
	 */
	public List<T> listByExample(T domain);

	/**
	 * 用于支持@Like, @Operator和@OrderBy 的分页查询
	 * @param domain
	 * @return
	 */
	public BasePage<T> listPageByExample(T domain);

	/**
	 * 根据实体查询easyui分页结果， 支持用metadata信息中字段对应的provider构建数据
	 * @param domain
	 * @return
	 */
	public EasyuiPageOutput listEasyuiPage(T domain, boolean useProvider) throws Exception;

	/**
	 * 用于支持@Like, @Operator和@OrderBy 的easyui分页查询, 支持用metadata信息中字段对应的provider构建数据
	 * @param domain
	 * @return
	 */
	public EasyuiPageOutput listEasyuiPageByExample(T domain, boolean useProvider) throws Exception;

}
