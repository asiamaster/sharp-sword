package com.dili.ss.mapper;

import com.dili.ss.dao.provider.ExactUpdateProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * 通用Mapper接口,精确更新，基于Selective设计
 * 必须实现IMybatisForceParams接口，并且设置setForceParams参数
 *
 * @param <T> 不能为空
 * @author wangmi
 */
@RegisterMapper
public interface UpdateByExampleExactMapper<T> {

    /**
     * 根据Example条件精确更新实体`record`，优雅解决以往需要强制更新null的问题<br/>
     * 比如要将某个字段改为null，可以这样:
     * <br/>
     * Map params = new HashMap();<br/>
     * params.put("field_name", null);<br/>
     * domain.setSetForceParams(params);
     * @param record
     * @param example
     * @return
     */
    @UpdateProvider(type = ExactUpdateProvider.class, method = "dynamicSQL")
    @Options(useCache = false, useGeneratedKeys = false)
    int updateByExampleExact(@Param("record") T record, @Param("example") Object example);
}