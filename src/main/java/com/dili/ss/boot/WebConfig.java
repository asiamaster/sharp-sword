package com.dili.ss.boot;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.dili.ss.servlet.CSRFInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
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

/**
 * 配置csrfInterceptor.enable=true启用CSRF攻击拦截<br></>
 * 拦截路径配置:CSRFInterceptor.path和CSRFInterceptor.excludePaths
 * Created by asiamaster on 2017/6/19 0019.
 */
@Configuration
@ConditionalOnExpression("'${web.enable}'=='true'")
//@EnableWebMvc不能使用@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
//	public class CSRFInterceptorConfig extends WebMvcConfigurationSupport {

	@Autowired
	private CSRFInterceptorProperties csrfInterceptorProperties;
	@Resource
	private CSRFInterceptor csrfInterceptor;

	/**
	 * 配置拦截器
	 *
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (csrfInterceptorProperties.getEnable()) {
			registry.addInterceptor(csrfInterceptor)
					.addPathPatterns(csrfInterceptorProperties.getPaths().toArray(new String[csrfInterceptorProperties.getPaths().size()]))
					.excludePathPatterns(csrfInterceptorProperties.getExcludePaths().toArray(new String[csrfInterceptorProperties.getExcludePaths().size()]));
		}
	}

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
//	@Overrid
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/**").addResourceLocations("/resources/**");
//	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		Iterator it =  converters.iterator();
		boolean added = false;
		while (it.hasNext()){
			Object obj = it.next();
			//去掉MappingJackson2HttpMessageConverter
			if(obj instanceof MappingJackson2HttpMessageConverter ){
				it.remove();
				//将FastJsonHttpMessageConverter加在MappingJackson2HttpMessageConverter，比xml解析器靠前，以提升性能
				if(!added){
					converters.add(new FastJsonHttpMessageConverter());
					added = true;
				}
			}
		}
		//如果没有MappingJackson2HttpMessageConverter，还是要加上FastJsonHttpMessageConverter
		if(!added){
			converters.add(new FastJsonHttpMessageConverter());
		}
	}


}
