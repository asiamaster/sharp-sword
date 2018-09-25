package com.dili.ss.base.mapper;

import com.dili.ss.dao.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * 通用Mapper接口,删除
 *
 * @param <T> 不能为空
 * @author wm
 */
@RegisterMapper
public interface DeleteMapper<T> {

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     *
     * @param record
     * @return
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "dynamicSQL")
    int delete(T record);

}