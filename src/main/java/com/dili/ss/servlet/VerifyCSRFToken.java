package com.dili.ss.servlet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Created by asiamaster on 2017/6/19 0019.
 * 跨站请求仿照注解
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyCSRFToken {
	/**
	 * 需要验证防跨站请求
	 *
	 */
	boolean verify() default true;
}