package com.dili.ss.metadata;

import java.util.List;
import java.util.Map;

/**
 * 值提供者统一接口
 *
 * @author asiamaster
 * @date 2017/5/29 0029
 */
public interface ValueProvider {

    String EMPTY_ITEM_TEXT = "-- 请选择 --";
    //metaMap中的key，获取字段名, 该值暂时是由easyui_extend.js中的bindMetadata方法提供的，后期考虑改为后台设置
    String FIELD_KEY = "field";
    //metaMap中的key，获取当前行的JSON数据，可能是DTO或Domain
    String ROW_DATA_KEY = "_rowData";
    //提供者的排序索引
    String INDEX_KEY = "index";
    //提供者bean id
    String PROVIDER_KEY = "provider";

    /**
     * 取下拉列表的选项
     * @param val 值对象
     * @param metaMap   meta信息
     * @return
     */
    List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta);

    /**
     * 取显示文本的值
     * @param val 值对象
     * @param metaMap   meta信息，包括:当前行数据:_rowData,当前字段名:_field及其它DTO中的meta信息
     * @param fieldMeta 当前字段的注解封装对象
     * @return
     */
    String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta);


}
