package com.dili.ss.datasource;

/**
 * 数据源切换模式，主从或多数据源
 * Created by asiamaster on 2017/8/10 0010.
 */
public enum SwitchMode {
	MULTI("1"), MASTER_SLAVE("2");

	private String code;

	SwitchMode(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	/**
	 * 根据类型编码获取SwitchMode
	 * @param code
	 * @return
	 */
	public static SwitchMode getSwitchModeByCode(String code){
		for(SwitchMode switchMode : SwitchMode.values()){
			if(switchMode.getCode().equals(code)){
				return switchMode;
			}
		}
		return null;
	}
}
