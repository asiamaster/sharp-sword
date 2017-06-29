package com.dili.ss.beetl;

import org.beetl.core.Context;
import org.beetl.core.VirtualAttributeEval;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asiamastor on 2017/1/23.
 */
@Component
@ConditionalOnExpression("'${beetl.enable}'=='true'")
public class VirtualAttributeEvalImpl implements VirtualAttributeEval {
    @Resource
    private Map<String, VirtualAttributeResolver> resolverMap = new HashMap<>();

    private Map<Class, VirtualAttributeResolver> resolverCacheMap = new HashMap<>();

    @PostConstruct
    public void init(){
        for(Map.Entry<String, VirtualAttributeResolver> entry : resolverMap.entrySet()) {
            resolverCacheMap.put(entry.getValue().resolveClass(), entry.getValue());
        }
    }

    @Override
    public boolean isSupport(Class aClass, String attrName) {
        return resolverCacheMap.containsKey(aClass);
    }

    @Override
    public Object eval(Object o, String attrName, Context context) {
        return resolverCacheMap.get(o.getClass()).resolve(o, attrName);
    }
}
