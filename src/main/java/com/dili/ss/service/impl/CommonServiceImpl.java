package com.dili.ss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.dao.CommonMapper;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.service.CommonService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by asiamaster on 2017/7/25 0025.
 */
@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private CommonMapper commonMapper;

	public List<ValuePair<?>> selectValuePair(String sql) {
//		Map paramMap = new HashMap();
//		paramMap.put("sql", sql);
		return commonMapper.selectValuePair(sql);
	}

	public List<JSONObject> selectJSONObject(String sql, Integer page, Integer rows) {
//		Map paramMap = new HashMap();
//		paramMap.put("sql", sql);
		//为了线程安全,请勿改动下面两行代码的顺序
		PageHelper.startPage(page, rows);
		return commonMapper.selectJSONObject(sql);
	}

	public void execute(String sql) {
		commonMapper.execute(sql);
	}

}
