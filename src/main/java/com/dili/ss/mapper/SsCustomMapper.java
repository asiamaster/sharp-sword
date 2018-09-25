package com.dili.ss.mapper;

import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * 利刃框架自定义mapper基类
 * Created by asiam on 2018/4/11 0011.
 */
@RegisterMapper
public interface SsCustomMapper<T> extends UpdateExactMapper<T>, InsertExactMapper<T>, ExpandSelectMapper<T> {
}
