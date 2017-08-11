package com.dili.ss.datasource;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 动态切换数据源
 * 在方法(优先)或类上使用，用于指定使用哪个数据源<br/>
 * 数据源在application.properties中配置<br/>
 * 示例:<br/>
 * spring.datasource.names=<B>ds1</B><br/>
 * spring.datasource.<B>ds1</B>.driver-class-name=com.mysql.jdbc.Driver<br/>
 * spring.datasource.<B>ds1</B>.url=jdbc:mysql://localhost:3306/electronic_clearing<br/>
 * spring.datasource.<B>ds1</B>.username=root<br/>
 * spring.datasource.<B>ds1</B>.password=123456<br/>
 *
 * Created by asiamaster on 2017/8/8 0008.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwitchDataSource {
	public static final String DEFAULT_DATASOURCE = "datasource";

	/**
	 * 数据源名称
	 * @return
	 */
	@AliasFor("name")
	String value() default SwitchDataSource.DEFAULT_DATASOURCE;

	/**
	 * 数据源名称
	 * @return
	 */
	@AliasFor("value")
	String name() default SwitchDataSource.DEFAULT_DATASOURCE;

	/**
	 * 切换数据源类型，默认为主库
	 *
	 * @return
	 */
	DataSourceType type() default DataSourceType.MASTER;
}
