package com.dili.ss.dao.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

/**
 * 扩展查询提供者
 * 这里主要扩展有两个部分:
 * 1. select 和 where 之间的部分，作替换
 * 2. where 和 order by 之间的部分，在原有的where sql和order by之间添加自定义sql，如group by having， 有sql注入风险
 * 参考tk.mybatis.mapper.provider.base.BaseSelectProvider和tk.mybatis.mapper.provider.ExampleProvider
 * Created by asiam on 2018/5/3 0002.
 */
public class ExpandSelectProvider extends MapperTemplate {

    public ExpandSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 查询一行
     * @param ms
     * @return
     */
    public String selectOneExpand(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(selectColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.whereAllIfColumns(entityClass, isNotEmpty()));
        sql.append(whereSuffixSql(false));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms
     * @return
     */
    public String selectExpand(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(selectColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.whereAllIfColumns(entityClass, isNotEmpty()));
        sql.append(whereSuffixSql(false));
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms
     * @return
     */
    public String selectByRowBoundsExpand(MappedStatement ms) {
        return selectExpand(ms);
    }

    /**
     * 根据主键进行查询
     *
     * @param ms
     */
    public String selectByPrimaryKeyExpand(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(selectColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.wherePKColumns(entityClass));
        sql.append(whereSuffixSql(false));
        return sql.toString();
    }

    /**
     * 查询全部结果
     *
     * @param ms
     * @return
     */
    public String selectAllExpand(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(selectColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    //---------------------------------------- example查询扩展 ----------------------------------------
    /**
     * 根据Example查询
     * 只添加了whereSuffixSql支持
     * 由于Example限制了只能查询实体中已有的列，目的是为了防止注入
     * 为了添加自定义的sql来实现sum, avg、floor、ceil、abs等函数查询，只能通过反射修改Example中的selectColumns属性
     * @param ms
     * @return
     */
    public String selectByExampleExpand(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder("SELECT ");
        if (isCheckExampleEntityClass()) {
            sql.append(SqlHelper.exampleCheck(entityClass));
        }
        sql.append("<if test=\"distinct\">distinct</if>");
        //支持查询指定列
        sql.append(SqlHelper.exampleSelectColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.exampleWhereClause());
        sql.append(whereSuffixSql(true));
        sql.append(SqlHelper.exampleOrderBy(entityClass));
        sql.append(SqlHelper.exampleForUpdate());
        return sql.toString();
    }

    /**
     * 根据Example查询一个结果
     * 只添加了whereSuffixSql支持
     * @param ms
     * @return
     */
    public String selectOneByExampleExpand(MappedStatement ms) {
        return selectByExampleExpand(ms);
    }

    //------------------------------------------------------------------------------------

    /**
     * where之后和orderby之前的自定义SQL块, 在原有的条件后面添加，用于exactSelect
     * 用法如: where id=xxx ${whereSuffixSql}(group by ... having或自己添加子查询等) order by id
     * @return
     */
    private String whereSuffixSql(boolean isExample){
        StringBuilder sql = new StringBuilder(64);
//        sql.append("<choose>");
//        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasSelectColumns(whereSuffixSql)\">");
//        sql.append("${whereSuffixSql}");
//        sql.append("</when>");
//        sql.append("</choose>");
        if(isExample){
//            sql.append("<if test=\"_parameter.whereSuffixSql != null\">");
            sql.append("<if test=\"@com.dili.ss.dao.util.OGNL@hasWhereSuffixSql(_parameter)\">");
            sql.append("${whereSuffixSql}");
        }else {
            sql.append("<if test=\"whereSuffixSql != null and whereSuffixSql != ''\">");
            sql.append("${whereSuffixSql}");
        }

        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 扩展查询列
     * select xxx,xxx...
     *
     * @param entityClass
     * @return
     */
    private String selectColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        //如果没有selectColumns属性，则查全部列；有selectColumns属性，则查询selectColumns的内容
//        sql.append("<choose><when test=\"selectColumns == null or selectColumns == ''\">");
//        sql.append(getAllColumns(entityClass));
//        sql.append("</when><otherwise>");
//        sql.append("${selectColumns}");
//        sql.append("</otherwise></choose>");
//        sql.append(" ");
        sql.append("<choose>");
        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasSelectColumns(selectColumns)\">");
        sql.append("<foreach collection=\"selectColumns\" item=\"selectColumn\" separator=\",\">");
        sql.append("${selectColumn}");
        sql.append("</foreach>");
        sql.append("</when>");
        sql.append("<otherwise>");
        sql.append(getAllColumns(entityClass));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass
     * @return
     */
    private String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnSet) {
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }

}