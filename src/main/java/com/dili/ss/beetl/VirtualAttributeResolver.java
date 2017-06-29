package com.dili.ss.beetl;

/**
 * 虚拟属性解析器接口
 * Created by asiamastor on 2017/1/23.
 */
public interface VirtualAttributeResolver {

    public String resolve(Object o, String attrName);

    //被解析Bean类
    public Class resolveClass();
}
