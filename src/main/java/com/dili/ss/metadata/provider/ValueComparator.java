package com.dili.ss.metadata.provider;

import java.util.Comparator;
import java.util.Map;

/**
 * 用于TreeSet或TreeMap的值排序比较器，该排序器用于LruProvider
 * Created by asiamaster on 2017/8/30 0030.
 */
public class ValueComparator implements Comparator<String> {

	Map<String, Integer> base;

	public ValueComparator(Map<String, Integer> map){
		this.base = map;
	}

	@Override
	public int compare(String key1, String key2) {
		Integer value1 = base.get(key1);
		Integer value2 = base.get(key2);
		if ((value2 - value1) > 0) {
			return 1;
		}else if (value2 - value1 ==0) { //值相同的时候，按升序排key
			return key1.toString().compareTo(key2.toString());
		}else {
			return -1;
		}
	}
}
