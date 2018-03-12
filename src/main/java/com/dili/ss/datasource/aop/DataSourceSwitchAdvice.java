package com.dili.ss.datasource.aop;

import com.dili.ss.datasource.SwitchDataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 数据源动态切换Advice,依赖@TargetDataSource注解
 * 支持嵌套切换数据源
 */
public class DataSourceSwitchAdvice implements MethodInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceSwitchAdvice.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		before(invocation);
		Object result = null;
		try {
			result = invocation.proceed();
		}finally {
			after(invocation);
		}
		return result;
	}

	private void before(MethodInvocation invocation) {
		Method method = invocation.getMethod();
		// omit the private method
		if (method.getModifiers() == Modifier.PRIVATE){
			return;
		}
		Class<?> declaringType = null;
		try {
			//由于TargetDataSource可能在类上，并且调用了父类的方法，所以这里只能强取targetClass，方法上的getDeclaringClass()只能取到父类
			Field field = ReflectiveMethodInvocation.class.getDeclaredField("targetClass");
			field.setAccessible(true);
			declaringType = (Class)field.get(invocation);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		//先从方法上取，再从类上取，不从接口上取(这里也会拦截到接口)
		SwitchDataSource targetDataSource = method.getAnnotation(SwitchDataSource.class);
		targetDataSource = targetDataSource == null ? declaringType.getAnnotation(SwitchDataSource.class) : targetDataSource;
		if (!DynamicRoutingDataSourceContextHolder.containsDataSource(targetDataSource.value())) {
//			DynamicRoutingDataSourceContextHolder.push(SwitchDataSource.DEFAULT_DATASOURCE);
			logger.error("数据源[{}]不存在，使用默认数据源 > {}", targetDataSource.value(), declaringType.getTypeName());
		} else {
			logger.debug("Use DataSource : {} > {}", targetDataSource.value(), declaringType.getTypeName());
			DynamicRoutingDataSourceContextHolder.push(targetDataSource.value());
		}
	}

	private void after(MethodInvocation invocation) {
//		Method method = invocation.getMethod();
//		if (method.getModifiers() == Modifier.PRIVATE)
//			return;
//		Class declaringType = method.getDeclaringClass();
//		TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
//		targetDataSource = targetDataSource == null ? (TargetDataSource)declaringType.getAnnotation(TargetDataSource.class) : targetDataSource;
//		logger.debug("Revert DataSource : {} > {}", targetDataSource.value(), declaringType.getTypeName());
		if(DynamicRoutingDataSourceContextHolder.getDataSourceType().isEmpty()) {
			DynamicRoutingDataSourceContextHolder.clear();
		}else {
			DynamicRoutingDataSourceContextHolder.pop();
		}
	}

}