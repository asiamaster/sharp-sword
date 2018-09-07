package com.dili.ss.util;

/**
 * Created by asiamastor on 2017/1/22.
 */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //获取applicationContext

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
        System.out.println("---------------SpringUtil------------------------------------------------------");
        System.out.println("ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext=" + SpringUtil.applicationContext);
        System.out.println("--------------------------------------------------------------------------------");
    }


    //通过name获取 Bean.
    public static Object getBean(String name) {
        if(getApplicationContext() != null) {
            return getApplicationContext().getBean(name);
        }else{
            return null;
        }
    }

    //获取泛型Bean
    public static <T> T getGenericBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }
    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }


    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}