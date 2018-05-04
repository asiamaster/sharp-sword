package com.dili.ss.dao.util;

import bsh.StringUtil;
import com.dili.ss.dao.ExampleExpand;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by asiam on 2018/5/4 0004.
 */
public abstract class OGNL {
    public static final String SAFE_DELETE_ERROR = "通用 Mapper 安全检查: 对查询条件参数进行检查时出错!";
    public static final String SAFE_DELETE_EXCEPTION = "通用 Mapper 安全检查: 当前操作的方法没有指定查询条件，不允许执行该操作!";

    public OGNL() {
    }

    public static boolean hasWhereSuffixSql(Object parameter) {
        if(parameter != null && parameter instanceof ExampleExpand) {
            ExampleExpand example = (ExampleExpand)parameter;
            if(StringUtils.isNotBlank(example.getWhereSuffixSql())) {
                return true;
            }
        }
        return false;
    }
}