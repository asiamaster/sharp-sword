package com.dili.ss.metadata.provider;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.dto.IDTO;
import com.dili.ss.metadata.BatchValueProvider;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ObjectMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.service.CommonService;
import com.dili.ss.util.POJOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户提供者
 */
@Component
public class SimpleBatchDisplayTextProvider extends BatchSqlDisplayTextProviderAdaptor {

    /**
     * 返回主DTO和关联DTO需要转义的字段名
     * Map中key为主DTO在页面(datagrid)渲染时需要的字段名， value为关联DTO中对应的字段名
     * @return
     */
    @Override
    protected Map<String, String> getEscapeFileds(Map metaMap){
        if(metaMap.get(ESCAPE_FILEDS_KEY) instanceof Map){
            return (Map)metaMap.get(ESCAPE_FILEDS_KEY);
        }else{
            String escapeField = (String)metaMap.get(ESCAPE_FILEDS_KEY);
            Map<String, String> map = new HashMap(1);
            map.put((String)metaMap.get(FIELD_KEY), escapeField);
            return map;
        }
    }
//    {
//        Map<String, String> excapeFields = new HashMap<>();
//        excapeFields.put("customerName", "name");
//        excapeFields.put("customerPhone", "phone");
//        return excapeFields;
//    }

    /**
     * 主DTO与关联DTO的关联(java bean)属性(外键)
     * @return
     */
    @Override
    protected String getFkField(Map metaMap){
        return (String)metaMap.get(FK_FILED_KEY);
    }

    /**
     * 关联(数据库)表名
     * @return
     */
    @Override
    protected String getRelationTable(Map metaMap){
        return (String)metaMap.get(RELATION_TABLE_KEY);
    }

    /**
     * 关联(数据库)表的主键名,默认为"id"
     * @return
     */
    @Override
    protected String getRelationTablePkField(Map metaMap){
        return metaMap.get(RELATION_TABLE_PK_FIELD_KEY) == null ? "id" : (String)metaMap.get(RELATION_TABLE_PK_FIELD_KEY);
    }

    @Override
    protected JSONObject getQueryParams(Map metaMap){
        return metaMap.get(QUERY_PARAMS_KEY) == null ? null : JSONObject.parseObject(metaMap.get(QUERY_PARAMS_KEY).toString());
    }
}