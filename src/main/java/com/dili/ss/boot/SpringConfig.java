package com.dili.ss.boot;

import com.dili.ss.util.SpringUtil;
import com.dili.ss.spring.RequestJsonParamMethodArgumentResolver;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * 此方案是把请求参数（JSON字符串）绑定到Java对象，，@RequestBody是绑定内容体到java对象的。<br/>
 * 问题描述：<br/>
 * 你好，对于如下的json数据，springmvc的数据绑定该如何做？
 * accessionDate   2012-11-21
 * deptIds [{"deptId":4,"isPrimary":true}]
 * email   ewer@dsfd.com
 * fax 3423432
 * gender  true
 * 其实就是在于deptIds的映射<br/>
 * 用法:<br/>
 * @ RequestMapping("/list")<br/>
 * public String list(@RequestJsonParam("deptIds") List<Integer> list)<br/>
 * Created by asiam on 2017/5/26
 */
//@Configuration
public class SpringConfig {

//    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfig.class);

//    @Bean(name="customRequestMappingHandlerAdapter", autowire = Autowire.BY_TYPE)
//    public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
//        RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
////        线程安全的访问session
//        requestMappingHandlerAdapter.setSynchronizeOnSession(true);
//        requestMappingHandlerAdapter.setCustomArgumentResolvers(Lists.newArrayList(new RequestJsonParamMethodArgumentResolver()));
//        return requestMappingHandlerAdapter;
//    }

    //此种方式可以覆盖现有的spring bean
//    @Bean
    public CommandLineRunner customRequestMappingHandlerAdapter(){
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                //获取BeanFactory
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RequestMappingHandlerAdapter.class);
                builder.addPropertyValue("synchronizeOnSession", true);
                List<HandlerMethodArgumentResolver> customArgumentResolvers = SpringUtil.getBean(RequestMappingHandlerAdapter.class).getCustomArgumentResolvers();
                customArgumentResolvers.add(new RequestJsonParamMethodArgumentResolver());
//                customArgumentResolvers.add(new RequestParamMethodArgumentResolver(true));
                builder.addPropertyValue("customArgumentResolvers", customArgumentResolvers);
                defaultListableBeanFactory.registerBeanDefinition("requestMappingHandlerAdapter", builder.getBeanDefinition());
                //        线程安全的访问session
//                requestMappingHandlerAdapter.setSynchronizeOnSession(true);
//                requestMappingHandlerAdapter.setCustomArgumentResolvers(Lists.newArrayList(new RequestJsonParamMethodArgumentResolver()));
            }
        };
    }


}
