package com.dili.ss.metadata;

import com.dili.ss.dto.IDTO;

import java.util.Map;

/**
 * 批量提供者的元数据信息
 */
public interface BatchProviderMeta extends IDTO {
    /**
     * 判断是否忽略大小写进行主表和外表数据关联
     * 子类可以实现，默认大小写敏感
     * @return
     */
    Boolean getIgnoreCaseToRef();
    void setIgnoreCaseToRef(Boolean ignoreCaseToRef);

    /**
     * getEscapeFiled和getEscapeFileds至少实现一个
     * 返回主DTO和关联DTO需要转义的字段名，可同时转义多个字段
     * Map中key为主DTO在页面(datagrid)渲染时需要的(field)字段名， value为关联DTO中对应的显示值的字段名
     * 示例:
     * {
     *     Map<String, String> excapeFields = new HashMap<>();
     *     excapeFields.put("customerName", "name");
     *     excapeFields.put("customerPhone", "phone");
     *     return excapeFields;
     * }
     */
    Map<String, String> getEscapeFileds();
    void setEscapeFileds(Map<String, String> escapeFileds);

    /**
     * getEscapeFiled和getEscapeFileds至少实现一个
     * 返回主DTO和关联DTO需要转义的字段名
     */
    String getEscapeFiled();
    void setEscapeFiled(String escapeFiled);

    /**
     * 选填
     * 关联(数据库)表的主键的字段名
     * 默认取id，子类可自行实现,
     */
    String getRelationTablePkField();
    void setRelationTablePkField(String relationTablePkField);


    /**
     * 选填
     * 主DTO与关联DTO的关联(java bean)属性(外键)
     * 先从field属性取，没取到再取_fkField属性
     */
    String getFkField();
    void setFkField(String fkField);
}
