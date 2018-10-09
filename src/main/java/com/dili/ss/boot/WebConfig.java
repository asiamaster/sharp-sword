package com.dili.ss.boot;

import com.dili.http.okhttp.java.CompileUtil;
import com.dili.http.okhttp.utils.B;
import com.dili.http.okhttp.utils.DESEncryptUtil;
import com.dili.ss.converter.JsonHttpMessageConverter;
import com.dili.ss.util.SystemConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 配置csrfInterceptor.enable=true启用CSRF攻击拦截<br></>
 * 拦截路径配置:CSRFInterceptor.path和CSRFInterceptor.excludePaths
 * Created by asiamaster on 2017/6/19 0019.
 */
@Configuration
@ConditionalOnExpression("'${web.enable}'=='true'")
//@EnableWebMvc //不能使用@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	public Environment env;

	@Bean
	public Converter<String, Date> addDateConvert() {
		return new Converter<String, Date>() {
			@Override
			public Date convert(String source) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = sdf.parse(source);
				} catch (ParseException e) {
					sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						date = sdf.parse(source);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				}
				return date;
			}
		};
	}

	@Bean
	public Converter<String, LocalDate> addLocalDateConvert() {
		return new Converter<String, LocalDate>() {
			@Override
			public LocalDate convert(String source) {
				return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
		};
	}

	@Bean
	public Converter<String, LocalDateTime> addLocalDateTimeConvert() {
		return new Converter<String, LocalDateTime>() {
			@Override
			public LocalDateTime convert(String source) {
				return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			}
		};
	}

	@Bean
	public Converter<String, Instant> addInstantConvert() {
		return new Converter<String, Instant>() {
			@Override
			public Instant convert(String source) {
				try {
					//毫秒数转为Instant
					return Instant.ofEpochMilli(Long.parseLong(source));
				} catch (NumberFormatException e) {
					//转换失败直接抛运行时异常
					return Instant.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).parse(source));
				}
			}
		};
	}

//	如果加了@EnableWebMvc 注解，只能自己添加添加加载处理了,还要注意是否需要添加webJars，所以最好不要添加@EnableWebMvc注解
//	增加@EnableWebMvc注解以后WebMvcAutoConfiguration中配置就不会生效
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/**").addResourceLocations("/static/**");
//	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		Iterator it =  converters.iterator();
		int index = -1;
		int tmp = 0;
		while (it.hasNext()){
			Object obj = it.next();
			//去掉MappingJackson2HttpMessageConverter
			if(obj instanceof MappingJackson2HttpMessageConverter ){
				it.remove();
				index = tmp;
			}
			if(index == -1) {
				tmp++;
			}
		}
		JsonHttpMessageConverter fastJsonHttpMessageConverter = new JsonHttpMessageConverter();
//		String dateFormat = env.getProperty("spring.fastjson.date-format");
//		if(StringUtils.isNotBlank(dateFormat)){
//			fastJsonHttpMessageConverter.setDateFormat(dateFormat);
//		}
		//将FastJsonHttpMessageConverter加在MappingJackson2HttpMessageConverter的位置，比xml解析器靠前，以提升性能
		if(index != -1){
			converters.add(index, fastJsonHttpMessageConverter);
		}
		//如果没有MappingJackson2HttpMessageConverter，还是要在最后加上FastJsonHttpMessageConverter
		else {
			converters.add(fastJsonHttpMessageConverter);
		}
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		B.b.s("args", argumentResolvers);
		B.b.dae("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");
	}

	/**
	 * 自定义返回类型
	 * @return
	 */
//	@Bean
//	public ResponseBodyWrapFactoryBean getResponseBodyWrap() {
//		return new ResponseBodyWrapFactoryBean();
//	}


	/**
	 * 统一错误页面处理
	 * 配置:
	 * error.page.default=error/default (错误页面的controller返回地址)
	 * error.page.indexPage=http://crm.diligrp.com:8085/crm/index.html (返回首页地址)
	 *
	 */
	@Bean
	public SimpleMappingExceptionResolver simpleMappingExceptionResolver(){
		SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
//		定义默认的异常处理页面
		simpleMappingExceptionResolver.setDefaultErrorView("error/default");
//		定义异常处理页面用来获取异常信息的变量名，如果不添加exceptionAttribute属性，则默认为exception
		simpleMappingExceptionResolver.setExceptionAttribute("exception");
//		定义需要特殊处理的异常，用类名或完全路径名作为key，异常页面名作为值
		Properties mappings = new Properties();
		mappings.put("java.lang.RuntimeException", SystemConfigUtils.getProperty("error.page.default", "error/default"));
		mappings.put("java.lang.Exception", SystemConfigUtils.getProperty("error.page.default", "error/default"));
		mappings.put("java.lang.Throwable", SystemConfigUtils.getProperty("error.page.default", "error/default"));
		simpleMappingExceptionResolver.setExceptionMappings(mappings);
		return simpleMappingExceptionResolver;
	}

}
