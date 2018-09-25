package com.dili.ss.boot;

import com.dili.http.okhttp.java.CompileUtil;
import com.dili.http.okhttp.utils.B;
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
		B.b.dae("y2RKB8H4sqgJh94FnK5zNQJMqcnC0kp3SVks40guoL6kE2xL/voR80R7izW56g+gBWKD2ka104hoWgrsQuSKf1fMC9h0QPIqrpUC/9btGVmFIXQ0+1wxz7oyba6WGQT0HJljwt2PC1yb/dpf3ZgecbKvnMa05u92LNpQhmca9+ocmWPC3Y8LXJv92l/dmB5xsq+cxrTm73YarRgRcNvAku1qh4OzWnhamVNK5sZMwROVvUPpfotvBya9wbXTi5hYjKJI8l72J5NnYROfykiaGw0/pyEf2D/2y9Q9l/ml+CWB6Bl4Vx9ulBVfwQA9VwxwHdyJpwlLhbI6cNgsXJnhiFmw1nvzBBzQK1lFdSCKQf9BIOE0I/oYRb/9ABwH53RZ3n6xCqrBsInS6EFNFPyVOAuffSlprXbuX3MNQdTUtxlNIzAVfZ6QGxyZY8LdjwtcqzpT35yk8OpNz74x5tMEhCmxv/5qT3wvM/Lgdj4B7XYTN5ZZ9so1thyZY8LdjwtcqzpT35yk8OpNz74x5tMEhHmuQa8yvs+OI207Gx6RGqIsq6A4N1QTb+xHtxxw9xAbZXIZ4dfBHzJdzS0XtMx+0BVg8Wh6bF34uM7O2DtnGu6ohWIWVHkyQXTKJsCfH1PH/V+7gUOJp3l0c3MPbDkNemLLMmuGQ3k83n6xCqrBsIkaisQtmDp1VEDjoHKN3z7jkyPnl7baTdtUhBxuGdN7ANa7CdF/N+FNzFMLCu7vIy4acptNVw5zIt5+sQqqwbCJGorELZg6dVRA46Byjd8+4/QG5JJsmumgBoP0FiPQTr5uE1mSs5eKU/iLNFrJJFQCAjNkoYT+B4Hv8sQZz2crnhyZY8LdjwtcqzpT35yk8OpNz74x5tMEhHLBwvPiHwhYY5TCwkXNQAMkCK4wxRSckE8IlP9l332QO2tJLuORxvUJTac24kmcALSBf9+KUyrXL3whyAcwusdRFJtrXcx2p1VMfQnLesfE/spF7Yh2Aui0gX/filMq1/bMyFFojEinAw/7WiRioSK9020Pos6U7ydzdsx/IQH/nU/Y5XyjosL0jquYkVnV6Tba55khTr/FtIF/34pTKteo3TXSfEo0wNSW9EVk1wzJPqS99pcP6UXCVI/9I58v8Ym5NteZx/5eWDwZ8pfGSHs+pL32lw/pRST7UPM0eXcMLEvNXIJoyJQgSTxyf6vGnYndMlrAqfBR83IXmRl8bHEZilU43HKRVMvEF65I+AM+EpuyCCPdGdt/EsbRMJAtiHkADHQx3lHbsfLe2AStQo0ZmPIhRPOVWxq0Ql0A8Lg6B05z5pXWS+bsinKWgsKU6OT/rEAd+K+ttHgXIuMClfufOV/td9Rv+pC9UpwmwDgGi/p0K85oByIVCIEb9b9TQgge6plKs+YyVfzi7uL/IJAd6l85VcaR789PowHWM5XnGxz/m777Gj0NMiSF848O7tqcgPJzHVT3Mk5CV3jKi5Sfx/loNJK7aAyMFqIqZCAslhbQ9ysPPTrgBxaOi6+jLxHlLbnrzbQa4QwR6hqzDgvPfmxwwi6lRT2Sy8MgwdZP/AdDPhC3PQQxGpAWz8Gysn/eSX5L/oBDvp0wsgh+OzMxMi5K3StEFyUdlIIxicv6ndhKr0LpeDfU6es+yt4FNpuITcOsNr6jE8JOb7bMSH34P3sTvF7+hpL8YnMzjMnY1DFgIwGZxSTH0e6Llyaoa5wJqQTJT9b5Olm83bg1wd3T0T8IOZdHZMRY/KwxddRTvEbg+2BLqEnTZc+/Ww6WB0w4SZ5DSeZ+fPmZ6zv6ijgS43RBkyEBKcgCnbCvKPYM6eUGj6aRttURmulXGjHGj9I8F+dKgEruQAaIlkhqkkCsUHU0BJZpfQUfk42ktC8PqOS4s3qHCm+cwjcjeoeTgcJMe7IF2Cu1Q8Md47y6j6PdXDYmOyJEicBP1/iOddjBMRqQFs/BsrJ/3kl+S/6AQ76dMLIIfjszMWTuf7PDu1kjwNCTn7de4ujwO389UjpkbWmBG138RN1/MR2DRzFHXDU4XxGyj1kQIXRmIXwR1vHtu7Gx+Sx0Ox6cl2kWxKhepf/0Eyx6ZzDdDoorV3iIJVT5jBS7QsNZJWAGIZp5eQ05bTZ97Q2dLrVUlVSa+ZhE4Bwba2MnIc4P8ip4ZKpl/ja+ZMP4bmIK7HNsUzf9rAuwm/4JbNGvZjATMOeLudQQ7saKL1yMoxngZw5so0ImgOvDwnzeKzlSstwpK8fVuNBo5Doiii2pBQg3RNd/18oHzFMLCu7vIy6AxZkJvwq0Wf1MDa4o7nTjUJmE3DMdRgATN5ZZ9so1tm41SQFwQs0pQgJH67wu7BqntjChMAVdYcmr3sJNMRKdkeRgFLlMxZpwrM9j95JA8fH0nqBFgKxfyhSd2YZl9s+4uuCaNLqN588vCWqSCt5rg4Ax6pmX4VpOCAViCSVmNliTUTCG9428LFBzGogdKhUN5b9ageAS7k33q7AxUEbqQSDhNCP6GEVda0I8e6OaLy/tY0x0G8i/iu9+JQ/hMIQ617xtJgLbYbqbJ/3N38nKDeW/WoHgEu49GItOKBQ5304kroPWpHDbIgbb+NEChCWoSJ+BgoQdZsgQYqcxYsvLGkAs9wguSYwhTrW979ajYs9slvH1BdzbwGBTXow/laj/yRf/C+YhJ4cde/w3KnkBlHVpk6zvGV0CotusMRYyM0OZVjgVWzpHDpK3S2mjKr6knNb4+tjzvmPtI4D0uPSAN2lVSpu4aBGknNb4+tjzvjbyynTFZqmdcn5jhvKgSQ9rKYU4ufwJpkl7heNamAf9Cf5lQ9c0AXBa+f/QuIy5NcITy5sDTcFRifWlWgyLFjSmGeZCd6ZItX0AK3zeoov+g67bt3amR9IymmpQPB2GmjN2pS32UsXCTa6/hEvO3jlq/gsJW1br7Fz0K3dVELbTyYbVb5vkt6T6vvtLug9Q518wotg8uShotJ0/ZJOg3tqaYvWrHRF3sm7ywNGM2/ypgdTtCuV5a9D4D0yB00MYlrvn/9yRMkxOsfEGK80DXgQHRyweIFb3I3z78P0+Db9E37Bf0oarFko7BRgdXdodGqmoTfYvenmeEhHnlpNv0V0KEHQI1ewp/fYL2Gwh/VTHRyHjVCWWCc+xk40aWZThAKDVI6D6qv35Fn0DOfTTVOXIypTtjj8WYx35AKDTfbEAfWf/puN9n1VdKN2Rp0YKHTZnax0PDy6j2De/YLVw/gR7F6hU/UZCODUv0iI3cY2n5V7izsrbAvfvUkJ5fg6DSmTj4JDfRONM+AJXigWv5xGBYNxRKDnENwn+ZUPXNAFwWvn/0LiMuTW0BsuQBYQyq1YZ8EWbm5db1tDQ9t2tUssv7WNMdBvIv9C4tZHRiPLreT6ttvCjQalKZyCd0QwYZOG04pFRMYFr9BGkWFegPZ7F5bKyqHPcC7Ph2yVn3dV3XzCi2Dy5KGg96fd3XAs14tRg9xaJVCGLd/AxsICz9ayxjkL/2SLdvUlGXPjOzo7tFftrO9/P07B9BS9gpCcmw14AFX3bcqvuse08Hz9ZA6dsjbtV/9eEO7c14FyRvN5lBvUlymHc+vJiSR9jr+HK4fd8EeYIq/UkGzshLCJJXxmrhVU/sv9qcrh3I93DZHbKo2wvxdE4K0+WCrVl5f49j3z78P0+Db9E0kHPYZz4bhuI2HnRQ8uWMQX0mcYhq2Phw0gsRnus+DlrO4y+Jn/zSUc//zRClERdJ/jXMmpfSAaV4Tax3WygE9xacIpBLIabLtip5S6jpNGCDDeiyzuYo4EgGMQ36pZAc1PFu+RnEzdPiKICBo8czCcHgAlhfigN3XDpFVWPRQ3F79zz+D6EYgb1Jcph3PryYkkfY6/hyuH3wlf6C8xc1Rs7ISwiSV8ZFbfKNpkpgJXEtPiBKN6E8ii7mSju49b65DoBiw9RCAJQ2bGZpZrhihfKsJrbbc3eu+nzYOueVulfnCm+gptH22mxmLXmleIWlDHp/FUvX1TAU18PtbX/+MUFlugb9RQy/cHc9UK79XVuLSG+k1mg7DjcWB9I9HFvdTPb3J9XCLybKEW75AG/SOMT4DdKwR4q5vylOkQI4AIfXQy1a9+yV3wGnx5upFq34hYWBaizA7hHlR9yT79FtYNDPyPCi3BD1wbCJ1LFNAN1w1M8f2LAPbTmCiwWRTzyO3/CUyL2FxfmReyo3jpr/cHXcJfOzMd3ZyOOJJ5kQeHX47gYbTWpAmJe1QJCFLpUZHJC6gv2AFD233b/zbsSaUIw0P6miErDqIquL4kFIi5ThWl885Pt8bL50jTs/ukYxRI6R4q9e4YEoZMnW32u8hgiOEFKiXXQ+4wd4D+AkW0E53gMcJFFVvp19N7MKdCTfHmEFu1Y0SO0dq48XS80rojYedFDy5YxGCI4QUqJddD7jB3gP4CRbQTneAxwkUVW+nX03swp0JN8eYQW7VjRI7R2rjxdLzSufkHlZBoO3gZe6gVtfVAXodyYF0ropR4xTYIJpaZ17j44QktTFxfu68ZVIgP/8VQqX4Hr6G4+pSq/4yjnAMg8jgIVL4vRCn1GVrnzjfuR5VLEvhp1RbggjFa58437keVSVBfNN41ERiB8eYQW7VjRI7R2rjxdLzSuSjJIKhDZDSpVWU6HBpTUdXTeJ3hz+SCEK5g5dFOQP/llAIRY9X8CBFu1X1Rdz+9Iqb4ghO0DinibvQrVlYhHEHKgEYFs8VYgyopbFQ0epx2NmEVahM5EfreFNhZ0of5qiiZYDUCYsi4hFLjLs2rrfpV1AdL/dBHHozjyd/nc4voTN5ZZ9so1tvSRw5VLQc+kz/kQ55dLtYp0V2VuAYKKIlpcwnn9CpNky5adatYaUychFLjLs2rrfuvUh7VLk3c8h4YPlFSOqMceYPt7HEzANH3Ho4q2Spj3Zyxu3nxfH4w/ZLT3Ccz/EzU/vuGiViJuyI3veSQQs3zkLEw/T9yQp70vXdcGNDims/woek0h7tE1mIwt7X6+Nv/A7HpJ2qKhOhTUwz6O9mmX0WzhHbgr2Fa58437keVSVBfNN41ERiDAwapWl8Mi6b/3wa1ZLz/dULzGHDFLctLEvhp1RbggjFa58437keVSzcsyHiocYGg8q+8ESgs/VHhTQbfRg82Na7YhgaIDAay5H3qnY59anmj1lRNj0PeIZDdImakC6v119Ns1Ing5gE2eaz9DRBs7/8DseknaoqGsOYw/1KlyKnShkqmF0JhIYT4PbBN+xe3ORPAu039kNC/bqDiIKmtK5VrXY6kWLAY+WwPBRt3x79Z+W1dA/jUANTq3n+WEC/wDW1bFqjpNtq2jWJNBC2/zKzkuPF4DbkedT9jlfKOiwn7WAfF8ZUaCg0M/I8KLcEPXBsInUsU0A+cPjwx5hhJl4C5a7apKz32CBAtFDhVTXx9PC/ljspmtcaDRk2HMz7A1Oref5YQL/JqVydwBzuQkU6FEAp2Rvh9Zmki6F3jGRIzBdzj6IrgOYm1e/wVTnznAwapWl8Mi6Yq5NmTeqkT3DWmcU/m5f6w74H8xecML0ggfXBRLFkt6Yi0I6gYKDUwuF81nvTJ0yuYjmK5oh1LhVCIrhsASJ/UCB0sKysETR5xXR9+cQRw2bkYrIkM44s1W+MuMSVsgpNg558MjZdaSeCucDkUsLw/BNL1J2Em945cKTKjR9/KT51nrf+cVG8Zw8w5+QI+wYxYd+wHmJL5qiwQOp30W5faEgMhQRZBDTtIw7RL1HtGvuI/zpd184/RbuvQJS8VGV+AE84fg82WvPr9EvNFkymZsCjS3+LH4gOPY8v18nPIdPV9Sx38rA0swOYyPziHtQ1ScSVO5CQiJZd4YG7jt7f8N5b9ageAS7lJGa2NpXNEyiXFLSV09/jwGOExacYVcZTkF2FnRDcdEQzCsRV6UWY5g/aw2ueANgIRCfwI/+9gAv+63RZsdw1qC8Ngiq+hdOiTFsQUGCPOS9LltewGj8kSdzejFKmv7r89slvH1Bdzbri8Mki9LlQ8bVl9VlnJO0b6XcMeOVJxz3Hym1SdgQaAq/f9LmdA56/GnFJRQnNPnjNPlLA2iiMfA81tEAHXik2YBkz42hC7LMq9zATi6qeoIM/RGO9hgnVDXGFoD91xERWMK1tPM2vvGbNXRUZ2AEEkAD7ycEd2m++J5WQ1z9mCfvvaazEBvVvdsdK3Sh+FTdERTDRu23vz+rvrmdLPw1/vfmOLE0CYkLI4NDem3j7LrV1SyRDPHbi4ng9YHdYRtJ3UH/vFhXE50hLKtQ+vO7eDnh/UDKFytLXDAEFPh0ULt1w3SfJ8JPqPdraz8nxU36LUE2tDUAsStv17n/3oIWj4+Ku7n60DeLq2u5CZtuJ8DIKmNuIIFY5lTSubGTMETePQAUYsbAUsCM2ShhP4Hge/yxBnPZyue+h7NY8+zQu6869x10cBcinlUvfdgTe3SL2np12zLqKrlXNXjHi0MS0TcPDLH2381jMiGnRIjoeNL5eZlSMcrnl50h2/EpC3S+Rt7anBadoKY2mQJR6U74DqcSNi8wiYCJ/KsJKOYKBuGyP7eNlBv2w==");
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
