package com.dili.ss.metadata;

import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/29 0029.
 */
public interface ValueProvider {

//    return theVal != null?theVal.toString():"";
    public static final String EMPTY_ITEM_TEXT = "-- 请选择 --";

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
     * @param metaMap   meta信息
     * @return
     */
    String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta);


}
