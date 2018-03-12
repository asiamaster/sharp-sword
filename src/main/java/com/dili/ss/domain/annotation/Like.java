package com.dili.ss.domain.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Created by asiamaster on 2017/5/26 0026.
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Like {
    public static final String LEFT="LEFT";
    public static final String RIGHT="RIGHT";
    public static final String BOTH="BOTH";
    String value() default BOTH;
}
