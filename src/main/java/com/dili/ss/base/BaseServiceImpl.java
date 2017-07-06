package com.dili.ss.base;


import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.BasePage;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.annotation.Like;
import com.dili.ss.domain.annotation.Operator;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Column;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 *	服务基类
 * Created by asiamastor on 2016/12/28.
 */
@Service
public abstract class BaseServiceImpl<T extends BaseDomain, KEY extends Serializable> implements BaseService<T, KEY> {
	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);

	@Autowired
	private MyMapper<T> mapper;

	/**
	 * 如果不使用通用mapper，可以自行在子类覆盖getDao方法
	 */
	public MyMapper<T> getDao(){
		return this.mapper;
	}

	public String redisKey(){
		return "microservice:base";
	}

	@Transactional(rollbackFor = Exception.class)
	public int insert(T t) {
		return mapper.insert(t);
	}

	@Transactional(rollbackFor = Exception.class)
	public int insertSelective(T t) {
		return mapper.insertSelective(t);
	}

	@Transactional(rollbackFor = Exception.class)
	public int batchInsert(List<T> list) {
		return mapper.insertList(list);
	}

	@Transactional(rollbackFor = Exception.class)
	@Caching(evict = {@CacheEvict(value = "rc", key = "#root.getTarget().redisKey()+':id:' + #key")})
	public int delete(KEY key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Transactional(rollbackFor = Exception.class)
	@Caching(evict = {@CacheEvict(value = "rc", key = "#root.getTarget().redisKey()+':id:' + #key")})
	public int delete(List<KEY> ids) {
		Type t = getClass().getGenericSuperclass();
		Class<T> entityClass = null;
		if(t instanceof ParameterizedType){
			Type[] p = ((ParameterizedType)t).getActualTypeArguments();
			entityClass = (Class<T>)p[0];
		}
		Example example = new Example(entityClass);
		example.createCriteria().andIn("id", ids);
		return getDao().deleteByExample(example);
	}

	@Transactional(rollbackFor = Exception.class)
	@Caching(evict = {@CacheEvict(value = "rc", key = "#root.getTarget().redisKey()+':id:' + #condtion.getId()")})
	public int updateSelective(T condtion) {
		return mapper.updateByPrimaryKeySelective(condtion);
	}

	@Transactional(rollbackFor = Exception.class)
	@Caching(evict = {@CacheEvict(value = "rc", key = "#root.getTarget().redisKey()+':id:' + #condtion.getId()")})
	public int update(T condtion) {
		return mapper.updateByPrimaryKey(condtion);
	}

	@Transactional(rollbackFor = Exception.class)
	public int batchUpdateSelective(List<T> list) {
		int count = 0;
		for(T t : list) {
			count+=mapper.updateByPrimaryKeySelective(t);
		}
		return count;
	}

	@Transactional(rollbackFor = Exception.class)
	public int batchUpdate(List<T> list) {
		int count = 0;
		for(T t : list) {
			count+=mapper.updateByPrimaryKey(t);
		}
		return count;
	}

	@Transactional(rollbackFor = Exception.class)
	public int saveOrUpdate(T t) {
		Long id = 0l;
		if (t instanceof BaseDomain) {
			id = ((BaseDomain) t).getId();
		} else {
			try {
				Class<?> clz = t.getClass();
				id = (Long) clz.getMethod("getId").invoke(t);
			} catch (Exception e) {
				LOGGER.warn("获取对象主键值失败!");
			}
		}
		if(id != null && id > 0) {
			return this.update(t);
		}
		return this.insert(t);
	}

	@Transactional(rollbackFor = Exception.class)
	public int saveOrUpdateSelective(T t) {
		Long id = 0l;
		if (t instanceof BaseDomain) {
			id = ((BaseDomain) t).getId();
		} else {
			try {
				Class<?> clz = t.getClass();
				id = (Long) clz.getMethod("getId").invoke(t);
			} catch (Exception e) {
				LOGGER.warn("获取对象主键值失败!");
			}
		}
		if(id != null && id > 0) {
			return this.updateSelective(t);
		}
		return this.insertSelective(t);
	}

	@Cacheable(value = "rc", key = "#root.getTarget().redisKey()+':id:' + #key", unless = "#result==null")
	public T get(KEY key) {
		return mapper.selectByPrimaryKey(key);
	}

	/**
	 * 根据实体查询
	 * @param condtion 查询条件
	 * @return
	 */
	public List<T> list(T condtion) {
		return mapper.select(condtion);
	}

	/**
	 * 根据实体分页查询
	 * @param domain
	 * @return
	 */
	public BasePage<T> listPage(T domain) {
//		T t = (T) BeanConver.copeBaseQueryBean(condtion, getSuperClassGenricType(getClass(), 0));
		//为了线程安全,请勿改动下面两行代码的顺序
		PageHelper.startPage(domain.getPage(), domain.getRows());
		List<T> list = mapper.select(domain);
		Page<T> page = (Page)list;
		BasePage<T> result = new BasePage<T>();
		result.setDatas(list);
		result.setPage(page.getPageNum());
		result.setRows(page.getPageSize());
		result.setTotalItem(Integer.parseInt(String.valueOf(page.getTotal())));
		result.setTotalPage(page.getPages());
		result.setStartIndex(page.getStartRow());
		return result;
	}

	/**
	 * 获取子类和所有超类的属性<br/>
	 * 过滤掉子类和父类同名的属性，以子类为主
	 * @param clazz
	 * @param fields
	 * @return
	 */
	protected List<Field> getDeclaredField(Class clazz, List<Field> fields){
		List<Field> addFields = Lists.newArrayList(Arrays.copyOf(clazz.getDeclaredFields(), clazz.getDeclaredFields().length));
		//过滤掉子类和父类同名的属性，以子类为主
		for(Field declaredField : clazz.getDeclaredFields()){
			for(int i=0; i<fields.size(); i++) {
				if (fields.get(i).getName().equals(declaredField.getName())){
					addFields.remove(i);
				}
			}
		}
		fields.addAll(addFields);
		if(clazz.getSuperclass() != null){
			getDeclaredField(clazz.getSuperclass(), fields);
		}

		return fields;
	}

	/**
	 * 用于支持like, order by 的查询
	 * @param domain
	 * @return
	 */
	public List<T> listByExample(T domain){
		Class tClazz = getSuperClassGenricType(getClass(), 0);
		Example example = new Example(tClazz);
		Example.Criteria criteria = example.createCriteria();
		List<Field> fields = new ArrayList<>();
		getDeclaredField(tClazz, fields);
		for(Field field : fields){
			Transient transient1 = field.getAnnotation(Transient.class);
			if(transient1 != null) continue;
			Like like = field.getAnnotation(Like.class);
			Operator operator = field.getAnnotation(Operator.class);
			Column column = field.getAnnotation(Column.class);
			String columnName = column == null ? field.getName() : column.name();
			Object value = null;
			try {
				field.setAccessible(true);
				value = field.get(domain);
			} catch (IllegalAccessException e) {
			}
			//没值就不拼接sql
			if(value == null) continue;
			if(like != null) {
				switch(like.value()){
					case Like.LEFT:
						criteria = criteria.andCondition(columnName + " like '%" + value + "' ");
						break;
					case Like.RIGHT:
						criteria = criteria.andCondition(columnName + " like '" + value + "%' ");
						break;
					case Like.BOTH:
						criteria = criteria.andCondition(columnName + " like '%" + value + "%' ");
						break;
					default :
						criteria = criteria.andCondition(columnName + " = '"+ value+"' ");
				}
			}else if(operator != null){
				if(operator.value().equals(Operator.IN)){
					StringBuilder sb = new StringBuilder();
					if(Collection.class.isAssignableFrom(field.getType())){
						for(Object o : (Collection)value){
							sb.append(", ").append(o);
						}
					}else if(field.getType().isArray()){
						for(Object o : ( (Object[])value)){
							sb.append(", ").append(o);
						}
					}else{
						sb.append(", ").append(value);
					}
					criteria = criteria.andCondition(columnName + " " + operator.value() + "("+sb.substring(1)+")");
				}else {
					criteria = criteria.andCondition(columnName + " " + operator.value() + " '" + value + "' ");
				}
			}else{
				criteria = criteria.andCondition(columnName + " = '"+ value+"' ");
			}
		}
		StringBuilder sb = new StringBuilder();
		for(Field field : tClazz.getFields()) {
			Transient transient1 = field.getAnnotation(Transient.class);
			if(transient1 != null) continue;
			OrderBy orderBy = field.getAnnotation(OrderBy.class);
			if(orderBy == null) continue;
			Column column = field.getAnnotation(Column.class);
			String columnName = column == null ? field.getName() : column.name();
			sb.append(","+columnName+" "+orderBy.value());
		}
		if(sb.length()>1) {
			example.setOrderByClause(sb.substring(1));
		}
		Integer page = domain.getPage();
		page = (page == null) ? Integer.valueOf(1) : page;
		Integer rows = domain.getRows() == null ? Integer.valueOf(Integer.MAX_VALUE) : domain.getRows();
		//为了线程安全,请勿改动下面两行代码的顺序
		PageHelper.startPage(page, rows);
		return mapper.selectByExample(example);
	}

	/**
	 * 用于支持like, order by 的分页查询
	 * @param domain
	 * @return
	 */
	public BasePage<T> listPageByExample(T domain){
		List<T> list = listByExample(domain);
		Page<T> page = (Page)list;
		BasePage<T> result = new BasePage<T>();
		result.setDatas(list);
		result.setPage(page.getPageNum());
		result.setRows(page.getPageSize());
		result.setTotalItem(Integer.parseInt(String.valueOf(page.getTotal())));
		result.setTotalPage(page.getPages());
		result.setStartIndex(page.getStartRow());
		return result;
	}

	/**
	 * 用于支持like, order by 的easyui分页查询
	 * @param domain
	 * @return
	 */
	public EasyuiPageOutput listEasyuiPageByExample(T domain, boolean useProvider) throws Exception {
		List<T> list = listByExample(domain);
		Page<T> page = (Page)list;
		List results = null;
		results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, list) : list;
		return new EasyuiPageOutput(Integer.parseInt(String.valueOf(page.getTotal())), results);
	}

	/**
	 * 根据实体查询easyui分页结果
	 * @param domain
	 * @return
	 */
	public EasyuiPageOutput listEasyuiPage(T domain, boolean useProvider) throws Exception {
		//为了线程安全,请勿改动下面两行代码的顺序
		PageHelper.startPage(domain.getPage(), domain.getRows());
		List<T> list = mapper.select(domain);
		Page<T> page = (Page)list;
		List results = null;
		results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, list) : list;
		return new EasyuiPageOutput(Integer.parseInt(String.valueOf(page.getTotal())), results);
	}

	public List<T> selectByExample(Object example){
		return getDao().selectByExample(example);
	}

	/**
	 * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
	 *
	 *@param clazz
	 *            clazz The class to introspect
	 * @param index
	 *            the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be
	 *         determined
	 */
	private Class<Object> getSuperClassGenricType(final Class clazz, final int index) {
		//返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		//返回表示此类型实际类型参数的 Type 对象的数组。
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}

}