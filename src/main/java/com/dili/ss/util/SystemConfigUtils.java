package com.dili.ss.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * 获取spring boot系统配置
 * Created by asiamaster on 2017/6/29 0029.
 */
public class SystemConfigUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigUtils.class);
	private static Properties props ;

	static{
		init();
	}

	private static void init(){
		try {
			Resource resource = new ClassPathResource("/application.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			String activeProfile = getProperty("spring.profiles.active");
			if(StringUtils.isBlank(activeProfile)){
				return;
			}
			resource = new ClassPathResource("/application-"+activeProfile+".properties");
			props.putAll(PropertiesLoaderUtils.loadProperties(resource));
		} catch (IOException e) {
			LOGGER.error("classpath下application.properties或者application-"+getProperty("spring.profiles.active")+".properties文件没找到!");
			e.printStackTrace();
		}
	}


	/**
	 * 获取属性
	 * @param key
	 * @return
	 */
	public static String getProperty(String key){
		return props == null ? null :  props.getProperty(key);
	}

	/**
	 * 获取属性
	 * @param key 属性key
	 * @param defaultValue 属性value
	 * @return
	 */
	public static String getProperty(String key,String defaultValue){
		return props == null ? null : props.getProperty(key, defaultValue);
	}

	/**
	 * 获取properyies属性
	 * @return
	 */
	public static Properties getProperties(){
		return props;
	}

}

