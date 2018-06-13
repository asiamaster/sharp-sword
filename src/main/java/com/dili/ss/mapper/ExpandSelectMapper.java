package com.dili.ss.mapper;

import com.dili.ss.dao.provider.ExpandSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.RowBounds;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * 通用扩展查询
 * 这里主要扩展有两个部分:
 * 1. select 和 where 之间的部分，作替换
 * 2. where 和 order by 之间的部分，在原有的where sql和order by之间添加自定义sql，如group by having， 有sql注入风险
 * @param <T> 不能为空
 * @author asiamaster
 */
@RegisterMapper
public interface ExpandSelectMapper<T> {

    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     *
     * @param record
     * @return
     */
    @SelectProvider(type = ExpandSelectProvider.class, method = "dynamicSQL")
    List<T> selectExpand(T record);

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param record
     * @return
     */
    @SelectProvider(type = ExpandSelectProvider.class, method = "dynamicSQL")
    T selectOneExpand(T record);

    /**
     * 根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号
     *
     * @param key
     * @return
     */
    @SelectProvider(type = ExpandSelectProvider.class, method = "dynamicSQL")
    T selectByPrimaryKeyExpand(Object key);

    /**
     * 查询全部结果
     *
     * @return
     */
    @SelectProvider(type = ExpandSelectProvider.class, method = "dynamicSQL")
    List<T> selectAllExpand();

    /**
     * 根据实体属性和RowBounds进行分页查询
     *
     * @param record
     * @param rowBounds
     * @return
     */
    @SelectProvider(type = ExpandSelectProvider.class, method = "dynamicSQL")
    List<T> selectByRowBoundsExpand(T record, RowBounds rowBounds);

    /**
     * 根据Example条件进行查询
     * 参数类型是com.dili.ss.dao.ExampleExpand
     * @param example
     * @return
     */
    @SelectProvider(type = ExpandSelectProvider.class, method = "dynamicSQL")
    List<T> selectByExampleExpand(Object example);

    /**
     * 根据Example条件进行查询
     *
     * @param example
     * @return
     */
    @SelectProvider(type = ExpandSelectProvider.class, method = "dynamicSQL")
    T selectOneByExampleExpand(Object example);
}