package com.dili.ss.service;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.metadata.ValuePair;

import java.util.List;

/**
 * 通用服务
 * Created by asiamaster on 2017/7/25 0025.
 */
public interface CommonService {

	List<ValuePair<?>> selectValuePair(String sql);

	List<JSONObject> selectJSONObject(String sql, Integer page, Integer rows);

	void execute(String sql);
}
