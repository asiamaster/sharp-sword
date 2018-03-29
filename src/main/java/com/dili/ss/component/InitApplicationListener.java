package com.dili.ss.component;

import com.alibaba.fastjson.JSON;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by asiam on 2018/3/23 0023.
 */
@Component
public class InitApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        JSON.DEFFAULT_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
    }
}