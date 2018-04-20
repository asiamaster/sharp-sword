package com.dili.ss.mapper;

/**
 * 精确更新
 * 必须实现IMybatisForceParams接口，并且设置setForceParams参数
 *
 * @param <T> 不能为空
 * @author wangmi
 */
public interface UpdateExactMapper<T> extends UpdateByExampleExactMapper<T>, UpdateByPrimaryKeyExactMapper<T> {

}