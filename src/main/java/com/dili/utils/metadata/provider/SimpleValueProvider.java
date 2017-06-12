package com.dili.utils.metadata.provider;

import com.dili.utils.SpringUtil;
import com.dili.utils.dao.CommonMapper;
import com.dili.utils.metadata.ValuePair;
import com.dili.utils.metadata.ValuePairImpl;
import com.dili.utils.metadata.ValueProvider;
import com.google.common.collect.Maps;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/29 0029.
 */
@Component
public class SimpleValueProvider implements ValueProvider {
    //    字段所在的表
    protected String table;
    //    基础数据值名称绑定到该下拉列表框。
    protected String valueField;
    //    基础数据字段名称绑定到该下拉列表框。
    protected String textField;
    // 值
    protected Object value;
    // 排序子句
    protected String orderByClause;
    //    查询参数
    protected Map<String, Object> queryParams = new HashedMap();

    public static final String TABLE_KEY = "table";
    public static final String VALUEFIELD_KEY = "valueField";
    public static final String TEXTFIELD_KEY = "textField";
    public static final String VALUE_KEY = "value";
    public static final String ORDER_BY_CLAUSE_KEY = "orderByClause";

    public SimpleValueProvider(){}

    private void buildParam(Map paramMap){
        setTable(paramMap.get(TABLE_KEY).toString());
        setValueField(paramMap.get(VALUEFIELD_KEY).toString());
        setTextField(paramMap.get(TEXTFIELD_KEY).toString());
        Object orderByClause = paramMap.get(ORDER_BY_CLAUSE_KEY);
        setValue(paramMap.get(VALUE_KEY));
        paramMap.remove(TABLE_KEY);
        paramMap.remove(VALUEFIELD_KEY);
        paramMap.remove(TEXTFIELD_KEY);
        paramMap.remove(VALUE_KEY);
        if(orderByClause != null) {
            setOrderByClause(orderByClause.toString());
            paramMap.remove(ORDER_BY_CLAUSE_KEY);
        }
        //清空缓存
        queryParams.clear();
        Object queryParams = paramMap.get("queryParams");
        if(queryParams != null) {
            setQueryParams((Map)queryParams);
        }
    }

    public List<ValuePair<?>> getLookupList(Object obj, Map paramMap){
        buildParam(paramMap);
        Map map = Maps.newHashMap();
        map.put("sql", buildSql());
        List<ValuePair<?>> data = SpringUtil.getBean(CommonMapper.class).select(map);
        data.add(0, new ValuePairImpl(EMPTY_ITEM_TEXT, null));
        return data;
    }

    private String buildSql(){
        StringBuffer sql = new StringBuffer();
        sql.append("select ").append(valueField).append(" value, ").append(textField).append(" text from ").append(table);
        if(queryParams!= null && !queryParams.isEmpty()) {
            sql.append(" where 1=1 ");
            for(Map.Entry<String, Object> entry : queryParams.entrySet()){
                sql.append("and ").append(entry.getKey()).append("='").append(entry.getValue()).append("' ");
            }
        }
        if(value != null){
           sql.append("and "+ getValueField()+"='"+value+"' ");
        }
        if(StringUtils.isNoneBlank(orderByClause)){
            sql.append(orderByClause);
        }
        return sql.toString();
    }

    /**
     * 取显示文本的值
     *
     * @return
     */
    public String getDisplayText(Object obj, Map paramMap){
        if(obj == null || obj.equals("")){
            return "";
        }
        paramMap.put(VALUE_KEY, obj);
        buildParam(paramMap);
        Map map = Maps.newHashMap();
        map.put("sql", buildSql());
        List<ValuePair<?>> data = SpringUtil.getBean(CommonMapper.class).select(map);
        if(data.isEmpty()) return "";
        return data.get(0).getText();
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }
}
