package com.dili.ss.dao.mapper;


import com.alibaba.fastjson.JSONObject;
import com.dili.ss.dto.IDTO;
import com.dili.ss.metadata.ValuePair;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/29 0029.
 */
public interface CommonMapper {
    /**
     * 查询值对
     * @param sql
     * @return
     */
    List<ValuePair<?>> selectValuePair(String sql);

    /**
     * 查询JSONObject
     * @param sql
     * @return
     */
    List<JSONObject> selectJSONObject(String sql);

    /**
     * 查询Map
     * @param sql
     * @return
     */
    List<Map> selectMap(String sql);

    /**
     * 查询DTO
     * @param sql
     * @return
     */
    <T extends IDTO> List<T> selectDto(@Param("value") String sql, @Param("resultType") Class<T> resultType);

    /**
     * 执行脚本
     * @param sql
     */
    void execute(String sql);
}
