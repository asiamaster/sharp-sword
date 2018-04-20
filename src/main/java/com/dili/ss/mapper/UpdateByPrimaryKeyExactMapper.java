package com.dili.ss.mapper;

import com.dili.ss.dao.ExactProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用Mapper接口,精确更新
 * 必须实现IMybatisForceParams接口，并且设置setForceParams参数
 *
 * @param <T> 不能为空
 * @author wangmi
 */
public interface UpdateByPrimaryKeyExactMapper<T> {

    /**
     * 根据主键精确更新实体`record`，优雅解决以往需要强制更新null的问题
     *
     * @param record
     * @return
     */
    @UpdateProvider(type = ExactProvider.class, method = "dynamicSQL")
    @Options(useCache = false, useGeneratedKeys = false)
    int updateByPrimaryKeyExact(T record);
}