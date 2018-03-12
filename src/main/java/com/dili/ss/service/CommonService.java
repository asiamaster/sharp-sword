package com.dili.ss.service;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.dto.IDTO;
import com.dili.ss.metadata.ValuePair;

import java.util.List;
import java.util.Map;

/**
 * 通用服务
 * Created by asiamaster on 2017/7/25 0025.
 */
public interface CommonService {

	List<ValuePair<?>> selectValuePair(String sql);

	List<JSONObject> selectJSONObject(String sql, Integer page, Integer rows);

	List<Map> selectMap(String sql, Integer page, Integer rows);

	<T extends IDTO> List<T> selectDto(String sql, Class<T> resultType);

	<T extends IDTO> List<T> selectDto(String sql, Class<T> resultType, Integer page, Integer rows);

	void execute(String sql);
}
