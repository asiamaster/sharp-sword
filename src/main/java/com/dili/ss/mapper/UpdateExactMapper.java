package com.dili.ss.mapper;

import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * 精确更新
 * 必须实现IMybatisForceParams接口，并且设置setForceParams参数
 *
 * @param <T> 不能为空
 * @author wangmi
 */
@RegisterMapper
public interface UpdateExactMapper<T> extends UpdateByExampleExactMapper<T>, UpdateByPrimaryKeyExactMapper<T> {

}