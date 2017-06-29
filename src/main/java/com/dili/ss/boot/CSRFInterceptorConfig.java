package com.dili.ss.boot;

import com.dili.ss.servlet.CSRFInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置csrfInterceptor.enable=true启用CSRF攻击拦截<br></>
 * 拦截路径配置:CSRFInterceptor.path和CSRFInterceptor.excludePaths
 * Created by asiamaster on 2017/6/19 0019.
 */
@Configuration
@ConditionalOnExpression("'${CSRFInterceptor.enable}'=='true'")
@ConfigurationProperties(prefix = "CSRFInterceptor")
public class CSRFInterceptorConfig extends WebMvcConfigurerAdapter {

	@Resource
	private CSRFInterceptor csrfInterceptor;
	//初始化以避免空
	@Value("${paths:/**}")
	private List<String> paths = new ArrayList<>();
	@Value("${excludePaths:/resources/**}")
	private List<String> excludePaths = new ArrayList<>();

	/**
	 * 配置拦截器
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(csrfInterceptor)
				.addPathPatterns(paths.toArray(new String[paths.size()]))
				.excludePathPatterns(excludePaths.toArray(new String[excludePaths.size()]));
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	public List<String> getExcludePaths() {
		return excludePaths;
	}

	public void setExcludePaths(List<String> excludePaths) {
		this.excludePaths = excludePaths;
	}
}
