package com.dili.ss.mapper;

import com.dili.ss.dao.provider.ExactInsertProvider;
import org.apache.ibatis.annotations.InsertProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * 通用Mapper接口,插入
 *
 * @param <T> 不能为空
 * @author wm
 */
@RegisterMapper
public interface InsertExactMapper<T> {
    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值<br/>
     * 默认功能同insertSelective<br/>
     * 必须是DTO接口，并且实现IMybatisForceParams接口，并且设置insertForceParams参数<br/>
     * 比如要将某个字段改为null，可以这样:<br/>
     * Map params = new HashMap();<br/>
     * params.put("field", null);<br/>
     * domain.setSetForceParams(params);
     * @param record
     * @return
     */
    @InsertProvider(type = ExactInsertProvider.class, method = "dynamicSQL")
    int insertExact(T record);
}