package com.dili.ss.domain.annotation;

import java.lang.annotation.*;

/**
 * sql运算符， 可能是and 和 or 两种
 * Created by asiamaster on 2017/11/29 0026.
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlOperator {
    public static final String AND = "and";
    public static final String OR = "or";
    String value() default AND;
}
