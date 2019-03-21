package com.dili.ss.activiti.boot;

import org.activiti.engine.impl.cfg.IdGenerator;

import java.util.UUID;

/**
 * @author wangmi
 * @date 2019-2-27 9:43:59
 * @since 1.0
 */
public class IdGen implements IdGenerator{

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Activiti ID 生成
     */
    @Override
    public String getNextId() {
        return IdGen.uuid();
    }

}
