package com.dili.ss.servlet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 跨站请求仿照注解 刷新CSRFToken
 * Created by asiamaster on 2017/6/19 0019.
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshCSRFToken {
	/**
	 * 刷新token
	 *
	 * @return
	 */
	boolean refresh() default true;
}
