package com.dili.ss.base.mapper;

import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.base.delete.DeleteByPrimaryKeyMapper;

/**
 * 通用Mapper接口,基础删除
 *
 * @param <T> 不能为空
 * @author asiamaster
 */
@RegisterMapper
public interface BaseDeleteMapper<T> extends
        DeleteMapper<T>,
        DeleteByPrimaryKeyMapper<T> {


}