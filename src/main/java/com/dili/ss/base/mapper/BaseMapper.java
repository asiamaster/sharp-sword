package com.dili.ss.base.mapper;

import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.base.BaseInsertMapper;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;

/**
 * 通用Mapper接口,其他接口继承该接口即可
 * <p/>
 * <p>这是一个例子，自己扩展时可以参考</p>
 * <p/>
 * <p>项目地址 : <a href="https://github.com/abel533/Mapper" target="_blank">https://github.com/abel533/Mapper</a></p>
 *
 * @param <T> 不能为空
 * @author asiamaster
 */
@RegisterMapper
public interface BaseMapper<T> extends
        BaseSelectMapper<T>,
        BaseInsertMapper<T>,
        BaseUpdateMapper<T>,
        BaseDeleteMapper<T> {

    /**
     * 根据id和版本号删除(乐观锁)
     * @param t
     * @return
     */
    default int deleteWithVersion(T t){
        int result = delete(t);
        if(result == 0){
            throw new RuntimeException("删除失败!");
        }
        return result;
    }

    /**
     * 根据id和版本号修改(乐观锁)
     * @param t
     * @return
     */
    default int updateByPrimaryKeyWithVersion(T t){
        int result = updateByPrimaryKey(t);
        if(result == 0){
            throw new RuntimeException("更新失败!");
        }
        return result;
    }

    /**
     * 根据id和版本号修改(乐观锁)
     * @param t
     * @return
     */
    default int updateByPrimaryKeySelectiveWithVersion(T t){
        int result = updateByPrimaryKeySelective(t);
        if(result == 0){
            throw new RuntimeException("更新失败!");
        }
        return result;
    }

}
