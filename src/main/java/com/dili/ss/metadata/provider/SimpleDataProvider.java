package com.dili.ss.metadata.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 简单数据提供者
 * Created by asiamaster on 2017/6/7 0007.
 */
@Component
public class SimpleDataProvider implements ValueProvider {

    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
        return null;
    }

    @Override
    public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
        if(obj == null || "".equals(obj)) return "";
        JSONArray ja = (JSONArray)metaMap.get("data");
        if(ja == null || ja.isEmpty()) return "";
        for(Object o : ja){
            JSONObject jo = (JSONObject)o;
            //处理-- 请选择-- 时value为空的情况
            if(jo.get("value") == null){
                continue;
            }
            if(jo.get("value").equals(obj) || jo.get("value").equals(obj.toString())){
                return jo.get("text").toString();
            }
        }
        return "";
    }
}