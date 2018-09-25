package com.dili.ss.dto;

import javax.persistence.Transient;
import java.util.Map;
import java.util.Set;

/**
 * mybatis强制传参接口
 * Created by asiam on 2018/2/2 0002.
 */
public interface IMybatisForceParams extends IDTO {

    /**
     * 按此map参数强制设值,用于exactUpdate
     * 此参数会在原有 "set xxx" 后面添加 ${key} = #{value}
     * @return
     */
    @Transient
    Map<String, Object> getSetForceParams();
    void setSetForceParams(Map<String, Object> setForceParams);

    /**
     * 按此map参数强制新增,用于exactInsert
     * 此参数会在原有 "insert (xxx,.." 后面添加 ${key}， 并在 "values(xxx,..." 后面添加#{value}
     * @return
     */
    @Transient
    Map<String, Object> getInsertForceParams();
    void setInsertForceParams(Map<String, Object> insertForceParams);

    /**
     * select和where之间自定义SQL块，替换原有的select块, 用于ExpandSelect
     * 用法如: select ${selectColumns} from 或自己添加子查询等
     * @return
     */
    @Transient
    Set<String> getSelectColumns();
    void setSelectColumns(Set<String> selectColumns);

    /**
     * where之后和orderby之前的自定义SQL块, 在原有的条件后面添加，用于exactSelect
     * 用法如: where id=xxx ${whereSuffixSql}(group by ... having或自己添加子查询等) order by id
     * @return
     */
    @Transient
    String getWhereSuffixSql();
    void setWhereSuffixSql(String whereSuffixSql);

    /**
     * 是否要检查注入攻击，默认不检查(仅用于BaseServiceImpl中的ListByExample)
     * 这里只对扩展查询中的select 和 where之间的字段进行检查
     * @return
     */
    @Transient
    Boolean getCheckInjection();
    void setCheckInjection(Boolean checkInjection);
}
