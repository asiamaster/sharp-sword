package com.dili.ss.dao;

import com.dili.ss.dto.IMybatisForceParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.*;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Set;

/**
 * 精确插入提供者
 * 参考tk.mybatis.mapper.provider.base.BaseInsertProvider
 * Created by asiam on 2018/2/2 0002.
 */
public class ExactProvider extends MapperTemplate {

    public ExactProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 精确插入
     * @param ms
     * @return
     */
    public String insertExact(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //Identity列只能有一个
        Boolean hasIdentityKey = false;
        //先处理cache或bind节点
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (StringUtil.isNotEmpty(column.getSequenceName())) {
                //sql.append(column.getColumn() + ",");
            } else if (column.isIdentity()) {
                //这种情况下,如果原先的字段有值,需要先缓存起来,否则就一定会使用自动增长
                //这是一个bind节点
                sql.append(SqlHelper.getBindCache(column));
                //如果是Identity列，就需要插入selectKey
                //如果已经存在Identity列，抛出异常
                if (hasIdentityKey) {
                    //jdbc类型只需要添加一次
                    if (column.getGenerator() != null && column.getGenerator().equals("JDBC")) {
                        continue;
                    }
                    throw new MapperException(ms.getId() + "对应的实体类" + entityClass.getCanonicalName() + "中包含多个MySql的自动增长列,最多只能有一个!");
                }
                //插入selectKey
                SelectKeyHelper.newSelectKeyMappedStatement(ms, column, entityClass, isBEFORE(), getIDENTITY(column));
                hasIdentityKey = true;
            } else if (column.isUuid()) {
                //uuid的情况，直接插入bind节点
                sql.append(SqlHelper.getBindValue(column, getUUID()));
            }
        }
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (StringUtil.isNotEmpty(column.getSequenceName()) || column.isIdentity() || column.isUuid()) {
                sql.append(column.getColumn() + ",");
            } else {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", isNotEmpty()));
            }
        }
        sql.append(buildInsertForceParams(entityClass));
        sql.append("</trim>");
        sql.append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            //优先使用传入的属性值,当原属性property!=null时，用原属性
            //自增的情况下,如果默认有值,就会备份到property_cache中,所以这里需要先判断备份的值是否存在
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
            } else {
                //其他情况值仍然存在原property中
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder(null, null, ","), isNotEmpty()));
            }
            //当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
            //序列的情况
            if (StringUtil.isNotEmpty(column.getSequenceName())) {
                sql.append(SqlHelper.getIfIsNull(column, getSeqNextVal(column) + " ,", isNotEmpty()));
            } else if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheIsNull(column, column.getColumnHolder() + ","));
            } else if (column.isUuid()) {
                sql.append(SqlHelper.getIfIsNull(column, column.getColumnHolder(null, "_bind", ","), isNotEmpty()));
            }
        }
        sql.append(buildValuesForceParams(entityClass));
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * 根据Example更新非null字段
     *
     * @param ms
     * @return
     */
    public String updateByExampleExact(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if(isCheckExampleEntityClass()){
            sql.append(SqlHelper.exampleCheck(entityClass));
        }
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass), "example"));
        sql.append(updateSetColumns(entityClass, "record", isNotEmpty()));
        sql.append(SqlHelper.updateByExampleWhereClause());
        return sql.toString();
    }

    /**
     * 通过主键精确更新不为null的字段
     * 有key，但value为null，则更新为null
     * 没有key，则不更新
     * @param ms
     * @return
     */
    public String updateByPrimaryKeyExact(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(updateSetColumns(entityClass, null, isNotEmpty()));
        sql.append(SqlHelper.wherePKColumns(entityClass));
        return sql.toString();
    }

    /**
     * update set列
     *
     * @param entityClass
     * @param entityName  实体映射名
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    private String updateSetColumns(Class<?> entityClass, String entityName, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable()) {
                sql.append(getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
            }
        }
        sql.append(buildSetForceParams(entityClass, entityName));
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param entityName
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    private String getIfNotNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (StringUtil.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            if (StringUtil.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 构建强制set语句
     * @param entityClass
     * @return
     */
//   <foreach collection="setForceParams" item="value" index="key" separator=",">
//       ${key} = #{value}
//   </foreach>
    private String buildSetForceParams(Class<?> entityClass, String entityName) {
        if(!IMybatisForceParams.class.isAssignableFrom(entityClass)){
            return "";
        }
        StringBuilder sql = new StringBuilder();
        entityName = StringUtils.isBlank(entityName) ? "" : entityName+".";
        sql.append("<foreach collection=\""+entityName+"setForceParams\" item=\"value\" index=\"key\" separator=\",\">");
        sql.append("${key} = #{value}");
        sql.append("</foreach>");
        return sql.toString();
    }

    /**
     * 构建insert部分强制参数
     * @param entityClass
     * @return
     */
    private String buildInsertForceParams(Class<?> entityClass) {
        if(!IMybatisForceParams.class.isAssignableFrom(entityClass)){
            return "";
        }
        StringBuilder sql = new StringBuilder();
        sql.append("<foreach collection=\"insertForceParams\" item=\"value\" index=\"key\" separator=\",\">");
        sql.append("${key}");
        sql.append("</foreach>");
        return sql.toString();
    }

    /**
     * 构建values部分强制参数
     * @param entityClass
     * @return
     */
    private String buildValuesForceParams(Class<?> entityClass) {
        if(!IMybatisForceParams.class.isAssignableFrom(entityClass)){
            return "";
        }
        StringBuilder sql = new StringBuilder();
        sql.append("<foreach collection=\"insertForceParams\" item=\"value\" index=\"key\" separator=\",\">");
        sql.append("${value}");
        sql.append("</foreach>");
        return sql.toString();
    }

}