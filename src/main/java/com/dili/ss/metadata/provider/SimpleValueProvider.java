package com.dili.ss.metadata.provider;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.dao.CommonMapper;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.ss.util.SpringUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/29 0029.
 */
@Component
@Scope("prototype")
public class SimpleValueProvider implements ValueProvider {
    //    字段所在的表
    private String table;
    //    基础数据值名称绑定到该下拉列表框。
    private String valueField;
    //    基础数据字段名称绑定到该下拉列表框。
    private String textField;
    // 值
    private Object value;
    // 排序子句
    private String orderByClause;
    //    查询参数
    private Map<String, Object> queryParams = new HashedMap();

    private static final String TABLE_KEY = "table";
    private static final String VALUEFIELD_KEY = "valueField";
    private static final String TEXTFIELD_KEY = "textField";
    private static final String VALUE_KEY = "value";
    private static final String ORDER_BY_CLAUSE_KEY = "orderByClause";
    private static final String QUERY_PARAMS_KEY = "queryParams";

    @Autowired
    private CommonMapper commonMapper;

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
        Object queryParams = paramMap.get(QUERY_PARAMS_KEY);
        if(queryParams != null) {
            setQueryParams(JSONObject.parseObject(queryParams.toString()));
        }
    }

    /**
     * 取列表的值
     * @param value 当前字段的值
     * @param paramMap 参数
     * @param fieldMeta
     * @return
     */
    public List<ValuePair<?>> getLookupList(Object value, Map paramMap, FieldMeta fieldMeta){
        buildParam(paramMap);
        List<ValuePair<?>> data = commonMapper.selectValuePair(buildSql());
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
     * @param value 当前字段的值
     * @param paramMap  参数
     * @param fieldMeta
     * @return
     */
    public String getDisplayText(Object value, Map paramMap, FieldMeta fieldMeta){
        if(value == null || value.equals("")){
            return "";
        }
        paramMap.put(VALUE_KEY, value);
        buildParam(paramMap);
        List<ValuePair<?>> data = SpringUtil.getBean(CommonMapper.class).selectValuePair(buildSql());
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
