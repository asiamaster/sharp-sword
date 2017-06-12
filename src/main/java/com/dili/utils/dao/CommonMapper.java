package com.dili.utils.dao;


import com.dili.utils.metadata.ValuePair;

import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/29 0029.
 */
public interface CommonMapper {
    List<ValuePair<?>> select(Map paramMap);
}
