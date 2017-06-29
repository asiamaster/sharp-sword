package com.dili.ss.retrofitful.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Created by asiamastor on 2016/11/28.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Restful {
    @AliasFor("value")
    String baseUrl() default "";

    @AliasFor("baseUrl")
    String value() default "";
}
