package com.dili.ss.datasource.aop;

import com.dili.ss.datasource.DataSourceType;
import com.dili.ss.datasource.SwitchDataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 数据源主从切换advice，依赖@Transactional和@SwitchDataSource注解.
 * 支持嵌套切换，遇到主库则内层调用都使用主库.
 * 支持从库负载均衡选择策略
 * 
 * @author asiamaster
 */
public class MasterSlaveDataSourceAdvice implements MethodInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceSwitchAdvice.class);

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
		if (method.getModifiers() == Modifier.PRIVATE)
			return;

		Method stackTopMethod = MasterSlaveContextHolder.getStackTopMethod();
		//如果栈顶方法为空，则放入当前方法
		if(stackTopMethod == null){
			// remember the out method, the out method should be marked before
			// invoking and should be clear after invoking.
			stackTopMethod = method;
			MasterSlaveContextHolder.pushOuterMethod(method);
		}else{
			//栈顶方法如果不可写继续把当前方法推入栈中，直到栈顶方法可写为止
			if(!MasterSlaveContextHolder.writable()){
				MasterSlaveContextHolder.pushOuterMethodByForce(method);
			}
		}
		//如果当前方法不可写，则需要重新从当前方法获取信息，判断是否可写
		if(!MasterSlaveContextHolder.writable()){
			boolean writable = false;
			// switch data source depending on the Transactional or DataSourceSwitch annotations
			Class<?> declaringType = stackTopMethod.getDeclaringClass();
			//检查类和方法上是否有Transactional注解
			boolean transactionalPresent = stackTopMethod.isAnnotationPresent(Transactional.class) || declaringType.isAnnotationPresent(Transactional.class);
			//先取方法上的@SwitchDataSource
			SwitchDataSource dataSourceSwitchMethod = stackTopMethod.getAnnotation(SwitchDataSource.class);
			//方法上没有@SwitchDataSource再取类上的
			SwitchDataSource dataSourceSwitch = dataSourceSwitchMethod == null ? declaringType.getAnnotation(SwitchDataSource.class): dataSourceSwitchMethod;
			writable = transactionalPresent ? true : dataSourceSwitch == null ? false : DataSourceType.MASTER == dataSourceSwitch.type();
			determinDataSource(writable);
		}
	}

	private void after(MethodInvocation invocation) {
		// clear the out method, the out method should be marked before
		// invoking and should be clear after invoking.
//		MasterSlaveContextHolder.pullOutMethod(invocation.getMethod());
		MasterSlaveContextHolder.clean();
	}

	private void determinDataSource(boolean writable) {
		// if the first access is writable already, then all the sub method
		// invocations will use the same dataSource
		if (MasterSlaveContextHolder.writable())
			return;
		// switch the dataSource depend on the current operation type
		if (writable)
			MasterSlaveContextHolder.write();
		else
			MasterSlaveContextHolder.read();

		LOGGER.info("according to dataSource switcher, try switch to {} dataSource", writable ? "writable" : "read only");
	}

}