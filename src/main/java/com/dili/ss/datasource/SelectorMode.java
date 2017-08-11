package com.dili.ss.datasource;

/**
 * 负载均衡器选择模式
 * Created by asiamaster on 2017/8/10 0010.
 */
public enum SelectorMode {
	ROUND_ROBIN("1"), WEIGHTED_ROUND_ROBIN("2");

	private String code;

	SelectorMode(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	/**
	 * 根据类型编码获取SelectorMode
	 * @param code
	 * @return
	 */
	public static SelectorMode getSelectorModeByCode(String code){
		for(SelectorMode switchMode : SelectorMode.values()){
			if(switchMode.getCode().equals(code)){
				return switchMode;
			}
		}
		return null;
	}
}
