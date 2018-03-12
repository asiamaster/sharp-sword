package com.dili.ss.metadata;

import java.util.List;
import java.util.Map;

/**
 * 批量值提供者统一接口
 *
 * @author asiamaster
 * @date 2017/5/29 0029
 */
public interface BatchValueProvider extends ValueProvider {

    /**
     * 批量设置显示列表
     * @param list 值列表
     * @param metaMap   meta信息
     * @return
     */
    void setDisplayList(List list, Map metaMap, ObjectMeta fieldMeta);


}
