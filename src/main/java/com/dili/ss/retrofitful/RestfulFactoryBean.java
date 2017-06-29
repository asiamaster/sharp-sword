package com.dili.ss.retrofitful;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by asiam on 2017/3/13 0013.
 */
public class RestfulFactoryBean implements FactoryBean {

    private Class intfClass;

    @Override
    public Object getObject() throws Exception {
        return intfClass.isInterface() ? RestfulUtil.getImpl(intfClass):null;
    }

    @Override
    public Class<?> getObjectType() {
        return intfClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class getIntfClass() {
        return intfClass;
    }

    public void setIntfClass(Class intfClass) {
        this.intfClass = intfClass;
    }
}
