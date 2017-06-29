package com.dili.ss.beetl;

import org.beetl.core.Format;
import org.springframework.stereotype.Component;

/**
 * Created by asiamaster on 2017/6/20 0020.
 */
@Component
public class NullFormat implements Format {

	@Override
	public Object format(Object data, String pattern) {
		return data == null ? "空值" :  data;
	}
}
