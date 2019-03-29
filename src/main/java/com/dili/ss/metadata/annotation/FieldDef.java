package com.dili.ss.metadata.annotation;

import java.lang.annotation.*;
import java.util.function.Function;

/**
 * DTO字段定义
 *
 * @author WangMi
 * @create 2010-6-2
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDef {

	/**
	 * 显示标签
	 *
	 * @return
	 */
	String label() default "";

	/**
	 * 最大长度
	 *
	 * @return
	 */
	int maxLength() default -1;

	/**
	 * 缺省值
	 *
	 * @return
	 */
	String defValue() default "";

	/**
	 * 值处理者
	 * @return
	 */
	Class<? extends Function> handler() default Function.class;

}
