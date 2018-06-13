package com.dili.ss.dao.provider;

import com.dili.ss.dto.IMybatisForceParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.version.VersionException;

import java.util.Set;

/**
 * 精确修改提供者
 * 包括精确查询，精确新增和精确修改
 * 参考tk.mybatis.mapper.provider.base.BaseUpdateProvider
 * Created by asiam on 2018/2/2 0002.
 */
public class ExactUpdateProvider extends MapperTemplate {

    public ExactUpdateProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 根据Example更新非null字段
     * 参考ExampleProvider.updateByExampleSelective
     * @param ms
     * @return
     */
    public String updateByExampleExact(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if(isCheckExampleEntityClass()){
            sql.append(SqlHelper.exampleCheck(entityClass));
        }

        //安全更新，Example 必须包含条件
        if (getConfig().isSafeUpdate()) {
            sql.append(SqlHelper.exampleHasAtLeastOneCriteriaCheck("example"));
        }
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass), "example"));
        sql.append(updateSetColumns(entityClass, "record", true, isNotEmpty()));
        sql.append(SqlHelper.updateByExampleWhereClause());
        return sql.toString();
    }

    /**
     * 通过主键精确更新不为null的字段
     * 有key，但value为null，则更新为null
     * 没有key，则不更新
     * 参考BaseUpdateProvider.updateByPrimaryKeySelective
     * @param ms
     * @return
     */
    public String updateByPrimaryKeyExact(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(updateSetColumns(entityClass, null, true, isNotEmpty()));
        sql.append(SqlHelper.wherePKColumns(entityClass, true));
        return sql.toString();
    }

    //---------------------------------------------------------------------------------------------------------------------

    /**
     * update set列
     *
     * @param entityClass
     * @param entityName  实体映射名
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    private String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //对乐观锁的支持
        EntityColumn versionColumn = null;
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (versionColumn != null) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                versionColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (column == versionColumn) {
                    Version version = versionColumn.getEntityField().getAnnotation(Version.class);
                    String versionClass = version.nextVersion().getCanonicalName();
                    //version = ${@tk.mybatis.mapper.version@nextVersionClass("versionClass", version)}
                    sql.append(column.getColumn())
                            .append(" = ${@tk.mybatis.mapper.version.VersionUtil@nextVersion(")
                            .append("@").append(versionClass).append("@class, ")
                            .append(column.getProperty()).append(")},");
                } else if (notNull) {
                    sql.append(SqlHelper.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
                }
            } else if(column.isId() && column.isUpdatable()){
                //set id = id,
                sql.append(column.getColumn()).append(" = ").append(column.getColumn()).append(",");
            }
        }
        sql.append(buildSetForceParams(entityClass, entityName));
        sql.append("</set>");
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

}