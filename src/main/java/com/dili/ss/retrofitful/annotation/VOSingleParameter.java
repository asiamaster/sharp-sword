package com.dili.ss.retrofitful.annotation;

import java.lang.annotation.*;

/**
 * VO单个参数对象<br/>
 * 用于restful调用中只有一个基本类型参数的场景，如:Long, Integer, String
 * Created by asiamastor on 2016/12/13.
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface VOSingleParameter {
}
