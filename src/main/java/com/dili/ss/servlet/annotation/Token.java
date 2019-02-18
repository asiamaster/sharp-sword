package com.dili.ss.servlet.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个接口是否需要在request.attr中设置幂等token
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    @AliasFor("value")
    String url() default "";

    @AliasFor("url")
    String value() default "";

}
