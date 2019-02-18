package com.dili.ss.uid.service;

import com.dili.ss.base.BaseService;
import com.dili.ss.uid.domain.BizNumber;
import com.dili.ss.uid.glossary.BizNumberType;

/**
 * 业务号服务
 */
public interface BizNumberService extends BaseService<BizNumber, Long> {

	/**
	 * 根据业务类型获取编号
	 * @param bizNumberType 业务类型，参照BizNumberType枚举
	 * @return
	 */
	String getBizNumberByType(BizNumberType bizNumberType);

}