package com.dili.ss.dto;

import java.util.Map;

/**
 * mybatis强制传参接口
 * Created by asiam on 2018/2/2 0002.
 */
public interface IMybatisForceParams extends IDTO {

    /**
     * 按此map参数强制设值
     * @return
     */
    Map<String, Object> getSetForceParams();
    void setSetForceParams(Map<String, Object> setForceParams);

    /**
     * 按此map参数强制新增
     * @return
     */
    Map<String, Object> getInsertForceParams();
    void setInsertForceParams(Map<String, Object> insertForceParams);

    /**
     * 按此map参数强制添加where条件
     * @return
     */
//    Map<String, Object> getWhereForceParams();
//    void setWhereForceParams(Map<String, Object> whereForceParams);
}
