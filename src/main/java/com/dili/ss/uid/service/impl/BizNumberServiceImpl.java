package com.dili.ss.uid.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.uid.dao.BizNumberMapper;
import com.dili.ss.uid.domain.BizNumber;
import com.dili.ss.uid.glossary.BizNumberType;
import com.dili.ss.uid.handler.BizNumberHandler;
import com.dili.ss.uid.service.BizNumberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务号生成服务
 *
 * @author asiamaster
 */
@Service
public class BizNumberServiceImpl extends BaseServiceImpl<BizNumber, Long> implements BizNumberService {

    @Autowired
    private BizNumberHandler bizNumberHandler;

    public BizNumberMapper getActualDao() {
        return (BizNumberMapper)getDao();
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    public String getBizNumberByType(BizNumberType bizNumberType) {
        if(bizNumberType == null){
            return null;
        }
        String range = bizNumberType.getRange();
        //默认自增步长为1
        if(StringUtils.isBlank(range)){
            range = "1";
        }else {
            //验证自增范围
            String[] ranges = range.split(",");
            if (ranges.length > 2) {
                throw new RuntimeException("非法自增步长范围");
            }
        }
        Long bizNumber = bizNumberHandler.getBizNumberByType(bizNumberType.getType(), bizNumberType.getDateFormat(), bizNumberType.getLength(), range);
        if(StringUtils.isBlank(bizNumberType.getDateFormat())){
            return bizNumberType.getPrefix() + String.format("%0" + bizNumberType.getLength() + "d", bizNumber);
        }else {
            return bizNumberType.getPrefix() + bizNumber;
        }
    }

}