package com.dili.utils.domain.annotation;

import java.lang.annotation.*;

/**
 * Created by asiamaster on 2017/5/26 0026.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderBy {
    public static final String ASC="ASC";
    public static final String DESC="DESC";
    String value() default "ASC";
}
