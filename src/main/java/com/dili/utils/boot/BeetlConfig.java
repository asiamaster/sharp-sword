package com.dili.utils.boot;

import com.dili.utils.beetl.CommonTagFactory;
import com.google.common.collect.Maps;
import org.beetl.core.*;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * beetl配置
 * Created by asiamastor on 2017/1/20.
 */
@Configuration
@ConditionalOnExpression("'${beetl.enable}'=='true'")
public class BeetlConfig  {

    @Autowired
    private VirtualAttributeEval virtualAttributeEval;
    @Autowired
    private Map<String, Function> functions;
    @Autowired
    private Map<String, Tag> tags;

    @Value("${server.context-path:}")
    private String contextPath;

    @Bean(initMethod = "init", name = "beetlGroupUtilConfiguration")
    public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {

        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        ResourcePatternResolver patternResolver = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader());
//        try {
//            // WebAppResourceLoader ����root·���ǹؼ�
//            WebAppResourceLoader webAppResourceLoader = new WebAppResourceLoader();
//            beetlGroupUtilConfiguration.setResourceLoader(webAppResourceLoader);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //此方式是闲大赋作者的提供的，但是在部署后，jar包路径要报错
        //因为部署后模板在jar中，所以不能用WebResourceLoader（它是按照文件路径加载的）
//        String root =  patternResolver.getResource("classpath:").getFile().toString();
//        WebAppResourceLoader webAppResourceLoader = new WebAppResourceLoader(root);
//        beetlGroupUtilConfiguration.setResourceLoader(webAppResourceLoader);
        //解决部署后找不到模板问题,但是要在下面的beetlViewResolver中配置beetlSpringViewResolver.setPrefix("/templates/");
        //并且在beetl.properties中改为RESOURCE.tagRoot =templates/htmltag,不然找不到html标签
        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader("/");
        beetlGroupUtilConfiguration.setResourceLoader(classpathResourceLoader);

        InputStream inputStream = BeetlConfig.class.getResourceAsStream("/beetlSharedVars.properties");
        Properties p = new Properties();
        try {
            if(inputStream != null) {
                p.load(inputStream);
                inputStream.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        p.put("contextPath",contextPath.equals("${server.context-path}")?"":contextPath);
        beetlGroupUtilConfiguration.setSharedVars((Map)p);
        beetlGroupUtilConfiguration.setVirtualAttributeEvals(Arrays.asList(virtualAttributeEval));
        beetlGroupUtilConfiguration.setFunctions(functions);
        beetlGroupUtilConfiguration.setTagFactorys(getTagFactoryMaps());
        beetlGroupUtilConfiguration.setConfigFileResource(patternResolver.getResource("classpath:beetl.properties"));
        Map<Class<?>, Format> typeFormats = Maps.newHashMap();
        typeFormats.put(Date.class, new org.beetl.ext.format.DateFormat());
        beetlGroupUtilConfiguration.setTypeFormats(typeFormats);
        return beetlGroupUtilConfiguration;
    }

    private Map<String, TagFactory> getTagFactoryMaps(){
        Map<String, TagFactory> tagFactoryMap = new HashMap<>(tags.size());
        for(Map.Entry<String, Tag> entry : tags.entrySet()) {
            tagFactoryMap.put(entry.getKey(), new CommonTagFactory(entry.getValue()));
        }
        return tagFactoryMap;
    }

    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(@Qualifier("beetlGroupUtilConfiguration") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setOrder(1);
        beetlSpringViewResolver.setPrefix("/templates/");
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }



}
