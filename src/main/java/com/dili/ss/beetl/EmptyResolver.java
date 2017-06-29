package com.dili.ss.beetl;

import org.springframework.stereotype.Component;

/**
 * Created by asiam on 2017/3/9 0009.
 */
@Component
public class EmptyResolver implements VirtualAttributeResolver {
    @Override
    public String resolve(Object o, String attrName) {
        return "";
    }

    @Override
    public Class resolveClass() {
        return this.getClass();
    }
}