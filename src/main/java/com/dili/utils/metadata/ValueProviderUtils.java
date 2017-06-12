package com.dili.utils.metadata;

import com.alibaba.fastjson.JSONObject;
import com.dili.utils.BeanConver;
import com.dili.utils.POJOUtils;
import com.dili.utils.SpringUtil;
import com.dili.utils.domain.BaseDomain;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 值提供者工具类
 *
 * @author ASIAMASTER
 * @create 2017-5-30
 */
@Component
public class ValueProviderUtils {

    @Autowired
    private Map<String, ValueProvider> valueProviderMap;
    /**
     * 根据providerId取提供者对象
     *
     * @param providerId
     * @return
     */
    public ValueProvider getProviderObject(String providerId) {
        return valueProviderMap.get(providerId);
    }

    /**
     * 取显示的文本值
     *
     * @return
     */
    public String getDisplayText(String providerId, Object obj, Map<String, Object> paramMap) {
        ValueProvider providerObj = valueProviderMap.get(providerId);
        return providerObj == null ? "" : providerObj.getDisplayText(obj, paramMap);
    }

    /**
     * 清除某个特定的Provider
     *
     * @param providerId
     */
    public void clearProvider(String providerId) {
        valueProviderMap.remove(providerId);
    }

    /**
     * 清除全部的缓冲
     */
    public void clearProviders() {
        valueProviderMap.clear();
    }

    /**
     * 取下拉项
     *
     * @return
     */
    public List<ValuePair<?>> getLookupList( String providerId, Object obj, Map<String, Object> paramMap) {
        ValueProvider providerObj = valueProviderMap.get(providerId);
        return providerObj == null ? Collections.EMPTY_LIST : providerObj.getLookupList(obj, paramMap);
    }

    //原始值保留前缀
    public static final String ORIGINAL_KEY_PREFIX = "$_";

    public static <T extends BaseDomain> List buildDataByProvider(T domain, List<T> list) throws Exception {
        if(domain.getMetadata() == null || domain.getMetadata().isEmpty()) return list;
        List<Map> results = new ArrayList<>(list.size());
        Map<String, ValueProvider> buffer = new HashMap<>();
        for(T t: list) {
            Map dataMap = transformObjectToMap(t);
            domain.getMetadata().forEach((k, v) -> {
                ValueProvider valueProvider = null;
                //key是字段field
                String key = k.toString();
                //value是provider相关的json对象
                JSONObject jsonValue = JSONObject.parseObject(v.toString());
                String providerBeanId  =jsonValue.get("provider").toString();
                jsonValue.remove("provider");
                if(buffer.containsKey(providerBeanId)){
                    valueProvider = buffer.get(providerBeanId);
                }else {
                    valueProvider = SpringUtil.getBean(providerBeanId, ValueProvider.class);
                    buffer.put(providerBeanId, valueProvider);
                }
                //保留原值，用于在修改时提取表单加载，但是需要过滤掉日期类型，
                // 因为前台无法转换Long类型的日期格式,并且也没法判断是日期格式
                if(!(dataMap.get(key) instanceof Date)) {
                    dataMap.put(ORIGINAL_KEY_PREFIX + key, dataMap.get(key));
                }
                dataMap.put(key, valueProvider.getDisplayText(dataMap.get(key), jsonValue));
            });
            results.add(dataMap);
        }
        return results;
    }

    /**
     * 把javaBean对象转换为Map键值对
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static Map<String, Object> transformObjectToMap(Object bean) throws Exception {
        Class<? extends Object> type = bean.getClass();
        Map<String, Object> returnMap = new HashMap<String, Object>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                }
            }
        }
        return returnMap;
    }
}
