package com.dili.ss.boot;

import com.dili.ss.servlet.CSRFInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * Created by asiam on 2018/3/12 0012.
 */
@Configuration
@ConditionalOnExpression("'${CSRFInterceptor.enable}'=='true'")
public class CSRFInterceptorConfig extends WebMvcConfigurationSupport {

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
}