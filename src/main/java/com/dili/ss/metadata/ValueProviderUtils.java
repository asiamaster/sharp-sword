package com.dili.ss.metadata;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.util.BeanConver;
import com.dili.ss.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
     * 根据providerId取显示的文本值
     *
     * @return
     */
    public String getDisplayText(String providerId, Object obj, Map<String, Object> paramMap) {
        ValueProvider providerObj = valueProviderMap.get(providerId);
        return providerObj == null ? "" : providerObj.getDisplayText(obj, paramMap, null);
    }

    /**
     * 根据FieldMeta取显示的文本值
     * @param fieldMeta
     * @param theVal
     * @param paramMap
     * @return
     */
    public String getDisplayText(FieldMeta fieldMeta,Object theVal, Map<String, Object> paramMap) {
        assert (fieldMeta.getProvider() != null);
        ValueProvider providerObj = valueProviderMap.get(fieldMeta.getProvider());
        return providerObj == null ? "" : providerObj.getDisplayText(theVal, paramMap, fieldMeta);
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
     * 根据providerId取下拉项
     */
    public List<ValuePair<?>> getLookupList( String providerId, Object val, Map<String, Object> paramMap) {
        ValueProvider providerObj = valueProviderMap.get(providerId);
        return providerObj == null ? Collections.EMPTY_LIST : providerObj.getLookupList(val, paramMap, null);
    }

    /**
     * 根据FieldMeta取下拉项
     * @param fieldMeta
     * @param theVal
     * @param paramMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ValuePair<?>> getLookupList(FieldMeta fieldMeta, Object theVal, Map<String, Object> paramMap) {
        assert (fieldMeta.getProvider() != null);
        ValueProvider providerObj = valueProviderMap.get(fieldMeta.getProvider());
        return providerObj == null ? Collections.EMPTY_LIST : providerObj.getLookupList(theVal, paramMap, fieldMeta);
    }

    //原始值保留前缀
    public static final String ORIGINAL_KEY_PREFIX = "$_";

    /**
     * 根据Provider构造表格显示数据，保留原始值，前缀为ORIGINAL_KEY_PREFIX
     * @param domain
     * @param list
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T extends IBaseDomain> List buildDataByProvider(T domain, List list) throws Exception {
        Map metadata = null;
        if(DTOUtils.isDTOProxy(domain)){
            metadata = domain.mget();
        }else{
            metadata = domain.getMetadata();
        }
        if(metadata == null || metadata.isEmpty()) return list;
        List<Map> results = new ArrayList<>(list.size());
        ObjectMeta objectMeta = MetadataUtils.getDTOMeta(DTOUtils.getDTOClass(domain));
        Map<String, ValueProvider> buffer = new HashMap<>();
        for(Object t: list) {
            Map dataMap = BeanConver.transformObjectToMap(t);
            metadata.forEach((k, v) -> {
                ValueProvider valueProvider = null;
                //key是字段field
                String key = k.toString();
                //value是provider相关的json对象
                JSONObject jsonValue = JSONObject.parseObject(v.toString());
                String providerBeanId  =jsonValue.get("provider").toString();
                jsonValue.remove("provider");
                jsonValue.put("_rowData", t);
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
                dataMap.put(key, valueProvider.getDisplayText(dataMap.get(key), jsonValue, objectMeta.getFieldMetaById(key)));
            });
            results.add(dataMap);
        }
        return results;
    }

    /**
     * 根据Provider构造表格显示数据，保留原始值，前缀为ORIGINAL_KEY_PREFIX
     * @param medadata
     * @param list
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T extends BaseDomain> List buildDataByProvider(Map medadata, List list) throws Exception {
        if(medadata == null || medadata.isEmpty()) return list;
        List<Map> results = new ArrayList<>(list.size());
        Map<String, ValueProvider> buffer = new HashMap<>();
        for(Object t: list) {
            Map dataMap = BeanConver.transformObjectToMap(t);
            medadata.forEach((k, v) -> {
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
                dataMap.put(key, valueProvider.getDisplayText(dataMap.get(key), jsonValue, null));
            });
            results.add(dataMap);
        }
        return results;
    }

    /**
     * 值提供者的工厂类
     */
    private static class ValueProviderFactory {
        protected static final Logger log = LoggerFactory.getLogger(ValueProviderFactory.class);
        // 缓冲对象
        private static final Map<Class<? extends ValueProvider>, ValueProvider> buffers = new ConcurrentHashMap<Class<? extends ValueProvider>, ValueProvider>();

        /**
         * 根据类型取提供者对象
         *
         * @return
         */
        public static ValueProvider getProviderObj(Class<? extends ValueProvider> providerClazz) {
            ValueProvider retval = buffers.get(providerClazz);
            if (retval == null) {
                try {
                    retval = providerClazz.newInstance();
                    buffers.put(providerClazz, retval);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
            return retval;
        }
    }


}
