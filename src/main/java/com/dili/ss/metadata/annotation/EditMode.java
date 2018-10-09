package com.dili.ss.metadata.annotation;

import com.dili.ss.metadata.FieldEditor;

import java.lang.annotation.*;


/**
 * 编辑方式的定义<br>
 * 必须随FieldDef进行定义,单独定义无效
 *
 * @author WangMi
 * @create 2010-6-2
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EditMode {

	/**
	 * 是否必须
	 *
	 * @return
	 */
	boolean required() default false;

	/**
	 * 是否可见
	 *
	 * @return
	 */
	boolean visible() default true;

	/**
	 * 是否只读
	 *
	 * @return
	 */
	boolean readOnly() default false;

	/**
	 * 所用的编辑器
	 */
	FieldEditor editor() default FieldEditor.Text;

	/**
	 * 编辑参数,编辑器的初始参数
	 *
	 * @return
	 */
	String params() default "";

	/**
	 * 在界面上出现的顺序
	 */
	int index() default Integer.MAX_VALUE;

	/**
	 * 是否支持排序(主要用于Table上对结果进行排序)
	 *
	 * @return
	 */
	boolean sortable() default true;

	boolean formable() default true;

	boolean gridable() default true;

	boolean queryable() default true;

	//===========================================   值模式  ===========================================

	/**
	 * 值提供者, spring bean id
	 *
	 * @return
	 */
	String provider() default "";

	/**
	 * 显示文本的字段名
	 *
	 * @return
	 */
	String txtField() default "";

}
