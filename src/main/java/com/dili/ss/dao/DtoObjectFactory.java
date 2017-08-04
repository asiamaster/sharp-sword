package com.dili.ss.dao;

import com.dili.ss.dto.DTOUtils;
import com.dili.ss.dto.IDTO;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * DTO对象工厂
 * 需要在mybatis-config.xml中配置:
 * <objectFactory type="com.dili.ss.dao.DtoObjectFactory">
 * </objectFactory>
 * Created by asiamaster on 2017/7/31 0031.
 */
public class DtoObjectFactory extends DefaultObjectFactory {

	private static final long serialVersionUID = 908294397084500018L;

	private Properties properties;
	//处理有参构造方法
	@Override
	public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes,
	                    List<Object> constructorArgs) {
		return super.create(type, constructorArgTypes, constructorArgs);
	}

	//处理默认的构造方法
	@Override
	public <T> T create(Class<T> type) {
		if(type.isInterface() && IDTO.class.isAssignableFrom(type)){
			return (T) DTOUtils.newDTO((Class<IDTO>)type);
		}else {
			return super.create(type);
		}
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
		super.setProperties(properties);
	}

	@Override
	public <T> boolean isCollection(Class<T> type) {
		return Collection.class.isAssignableFrom(type);
	}

}
