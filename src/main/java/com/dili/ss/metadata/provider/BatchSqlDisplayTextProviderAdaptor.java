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

import java.util.*;

/**
 * 批量本地sql提供者
 */
@Component
public abstract class BatchSqlDisplayTextProviderAdaptor extends BatchDisplayTextProviderAdaptor {

    @Override
    protected List<Map> getFkList(List<String> relationIds, Map metaMap) {
        StringBuilder sqlBuilder = new StringBuilder("select * from ");
        sqlBuilder.append(getRelationTable(metaMap));
        sqlBuilder.append(" where ");
        sqlBuilder.append(getRelationTablePkField(metaMap));
        sqlBuilder.append(" in('");
        sqlBuilder.append(relationIds.get(0));
        sqlBuilder.append("'");
        for(int i=1; i<relationIds.size(); i++){
            sqlBuilder.append(",'").append(relationIds.get(i)).append("'");
        }
        sqlBuilder.append(")");
        //如果有json查询参数，则组装查询条件
        JSONObject conditionJson = getQueryParams(metaMap);
        if(conditionJson != null && !conditionJson.isEmpty()) {
            for(Map.Entry<String, Object> conditionEntry : Collections.unmodifiableMap(conditionJson).entrySet()){
                sqlBuilder.append(" and `").append(conditionEntry.getKey()).append("`='").append(conditionEntry.getValue()).append("'");
            }
        }
        return commonService.selectMap(sqlBuilder.toString(), 1, relationIds.size());
    }

    /**
     * 关联(数据库)表名
     * @return
     */
    protected abstract String getRelationTable(Map metaMap);

    /**
     * 查询条件json
     * @return
     */
    protected JSONObject getQueryParams(Map metaMap){
        return null;
    }
}