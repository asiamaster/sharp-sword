package com.dili.ss.datasource.aop;

import com.dili.ss.datasource.SwitchDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 切换数据源Advice
 * 由于AspectJ语法拦截不了类上的注解时，如果调用父类方法将失效，暂时不用此类,使用DataSourceSwitchAdvisor拦截
 * Created by asiamaster on 2017/8/8 0008.
 */
//@Aspect
//@Order(-1)// 保证该AOP在@Transactional之前执行
//@Component
public class DynamicRoutingDataSourceAspect {

	private static final Logger logger = LoggerFactory.getLogger(DynamicRoutingDataSourceAspect.class);

//	@Around("execution(String com.dili.ec.controller.DeviceController.list(org.springframework.ui.ModelMap, com.dili.ec.domain.Device))")
//	public void test(JoinPoint point){
//		TargetDataSource tds = (TargetDataSource)point.getSignature().getDeclaringType().getAnnotation(TargetDataSource.class);
//		System.out.println(tds.value());
//	}

//	@Pointcut("@annotation(com.dili.ec.datasource.TargetDataSource)")
//	@Pointcut("@target(com.dili.ec.datasource.TargetDataSource)")
	public void withinTargetDataSource() {
	}

//	@Before(value = "@annotation(ds)")
//	@Before(value = "@target(com.dili.ec.datasource.TargetDataSource) || @annotation(com.dili.ec.datasource.TargetDataSource)")
	public void before(JoinPoint point) {
//		TargetDataSource ds = (TargetDataSource)point.getSignature().getDeclaringType().getAnnotation(TargetDataSource.class);
//		String dsId = ds.value();
//		if (!DynamicRoutingDataSourceContextHolder.containsDataSource(dsId)) {
//			logger.error("数据源[{}]不存在，使用默认数据源 > {}", ds.value(), point.getSignature());
//		} else {
//			logger.debug("Use DataSource : {} > {}", ds.value(), point.getSignature());
//			DynamicRoutingDataSourceContextHolder.setDataSourceType(ds.value());
//		}
	}

//	@Around( "withinTargetDataSource()")
//	@Around("execution(* com.dili.ec.controller.*.*(..))")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		System.out.println("around:"+point.getTarget());
		Object result = point.proceed();
		return result;
	}
//	@After(value = "@within(com.dili.ec.datasource.TargetDataSource) && args(ds)", argNames = "point,ds")
	public void restoreDataSource(JoinPoint point, SwitchDataSource ds) {
		logger.debug("Revert DataSource : {} > {}", ds.value(), point.getSignature());
		DynamicRoutingDataSourceContextHolder.clear();
	}
}
