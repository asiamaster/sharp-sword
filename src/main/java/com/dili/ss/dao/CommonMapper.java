package com.dili.ss.dao;


import com.alibaba.fastjson.JSONObject;
import com.dili.ss.metadata.ValuePair;

import java.util.List;

/**
 * Created by asiamaster on 2017/5/29 0029.
 */
public interface CommonMapper {
    List<ValuePair<?>> selectValuePair(String sql);

    List<JSONObject> selectJSONObject(String sql);

    void execute(String sql);
}
