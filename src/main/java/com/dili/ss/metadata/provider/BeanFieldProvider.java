package com.dili.ss.metadata.provider;

import com.dili.ss.metadata.*;
import com.dili.ss.util.POJOUtils;
import com.dili.ss.util.ReflectionUtils;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 根据metadata取Bean字段下拉和显示值
 * Created by asiamaster on 2017/7/20
 */
@Component
public class BeanFieldProvider implements ValueProvider {

    private static final String QUERY_PARAMS_KEY = "queryParams";

    /**
     * 取下拉列表<br/>
     * value为数据库字段, 先取@javax.persistence.Column，取不到就直接将字段名按驼峰转下划线作为数据库对应字段<br/>
     * text为FieldDef中定义的label
     * @param obj   bean class全名
     * @param metaMap
     * @param fieldMeta
     * @return
     */
    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
        Object className = metaMap.get(QUERY_PARAMS_KEY);
        if(className == null){
            return null;
        }
        List<ValuePair<?>> valuePairs =Lists.newArrayList();
        try {
            Class dtoClass = Class.forName(className.toString());
            ObjectMeta objectMeta = MetadataUtils.getDTOMeta(dtoClass);
            for(Method method : dtoClass.getMethods()) {
                //从get方法上取FieldDef和EdisMode注解信息
                if(POJOUtils.isGetMethod(method)){
                    String fieldName = POJOUtils.getBeanField(method);
                    FieldMeta fieldMeta1 = objectMeta.getFieldMetaById(fieldName);
                    //数据库的下划线规则字段名
                    String dbFieldName = null;

                    //接口只能从getter方法取
                    if(dtoClass.isInterface()){
                        if(method.getAnnotation(Transient.class) != null){
                            continue;
                        }
                        Column column = method.getAnnotation(Column.class);
                        if (column != null) {
                            dbFieldName = column.name();
                        } else {
                            dbFieldName = POJOUtils.humpToLineFast(fieldName);
                        }
                    }else {
                        //java bean类取getter方法对应的字段(包括搜索父类)，以获取数据库字段名
                        Field field = ReflectionUtils.getAccessibleField(dtoClass, fieldName);
//                    不添加Transient字段和没找到getter对应的字段
                        if (field == null || field.getAnnotation(Transient.class) != null) {
//                        dbFieldName = POJOUtils.humpToLineFast(fieldName);
                            continue;
                        } else { //找到getter对应的字段则取字段上的@javax.persistence.Column注解
                            Column column = field.getAnnotation(Column.class);
                            if (column != null) {
                                dbFieldName = column.name();
                            } else {
                                dbFieldName = POJOUtils.humpToLineFast(fieldName);
                            }
                        }
                    }
                    //如果没有定义FieldDef注解则直接取getter方法转换的字段名为显示值
                    if(fieldMeta1 == null) {
                        valuePairs.add(new ValuePairImpl<String>(fieldName, dbFieldName));
                    }else{ //取FieldDef注解的label为显示值
                        String label = fieldMeta1.getLabel() == null ? fieldMeta1.getName() : fieldMeta1.getLabel();
                        valuePairs.add(new ValuePairImpl<String>(label, dbFieldName));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return valuePairs;
    }

    @Override
    public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
        if(obj == null || "".equals(obj)) return "";
        return obj.toString();
    }
}
