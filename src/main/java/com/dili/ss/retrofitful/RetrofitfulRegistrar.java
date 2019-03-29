package com.dili.ss.retrofitful;

import com.dili.http.okhttp.utils.B;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 基于okhttp重新封装，Retrofit风格的restful RPC配置<br></>
 * 主要用于直接支持接口+注解=rpc spring bean<br></>
 * 默认支持
 * Created by asiamastor on 2017/1/11.
 */
public class RetrofitfulRegistrar implements ImportBeanDefinitionRegistrar {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        B.b.dae("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");
        Set<String> basePackages = getBasePackages(annotationMetadata);
        for (String basePackage : basePackages) {
            Resource rootResource = getRootResource(basePackage);
            Resource[] resources = getResources(basePackage);
            for (Resource resource : resources) {
                String classFullName = null;
//                ClassPathResource classPathResource = null;
                try {
//                    classPathResource = new ClassPathResource(resource.getURL().getPath());
                    classFullName = getClassNameByResource(resource, rootResource.getURL(), basePackage);
                    Class intfClass = Class.forName(classFullName);
                    if (!intfClass.isInterface() || intfClass.getAnnotation(Restful.class) == null) {
                        continue;
                    }
//                System.out.println("jar包里面的类:"+ClassUtils.convertResourcePathToClassName(result));

                    //类的全路径
//                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(intfClass);
                    //向里面的属性注入值，提供get set方法
//                dataSourceBuider.addPropertyValue("name", "wangmi");
                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                    beanDefinition.setBeanClass(RestfulFactoryBean.class);
                    beanDefinition.getPropertyValues().add("intfClass", intfClass);
                    beanDefinition.setSynthetic(true);
                    //注册Bean
                    beanDefinitionRegistry.registerBeanDefinition(buildDefaultBeanName(classFullName), beanDefinition);
                }
                catch (IOException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
//        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
//        beanDefinition.setBeanClass(BaseDomain.class);
//        beanDefinition.setSynthetic(true);
//        beanDefinitionRegistry.registerBeanDefinition("baseDomain", beanDefinition);
    }

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
//        TODO 从@RestfulServiceScan注解中获取
        String basePackage = "";

        //Bean构建  BeanService.class 要创建的Bean的Class对象
        BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(BaseDomain.class);
        dbf.registerBeanDefinition("baseDomain", dataSourceBuider.getBeanDefinition());
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map attributes = importingClassMetadata.getAnnotationAttributes(RestfulScan.class.getCanonicalName());
        HashSet basePackages = new HashSet();
        String[] var4 = (String[]) ((String[]) attributes.get("value"));
        int var5 = var4.length;

        int var6;
        String clazz;
        for (var6 = 0; var6 < var5; ++var6) {
            clazz = var4[var6];
            if (StringUtils.hasText(clazz)) {
                basePackages.add(clazz);
            }
        }

        var4 = (String[]) ((String[]) attributes.get("basePackages"));
        var5 = var4.length;

        for (var6 = 0; var6 < var5; ++var6) {
            clazz = var4[var6];
            if (StringUtils.hasText(clazz)) {
                basePackages.add(clazz);
            }
        }

        Class[] var8 = (Class[]) ((Class[]) attributes.get("basePackageClasses"));
        var5 = var8.length;

        for (var6 = 0; var6 < var5; ++var6) {
            Class var9 = var8[var6];
            basePackages.add(ClassUtils.getPackageName(var9));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        return basePackages;
    }

    /**
     * 获取根资源
     * 找不到返回空
     */
    private Resource getRootResource(String basePackage) {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String basePackagePath = ClassUtils.convertClassNameToResourcePath(basePackage);
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:" + basePackagePath + "/");
            return resources.length > 0 ? resources[0] : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 扫描所有资源
     * 找不到返回空
     */
    private Resource[] getResources(String basePackage) {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String basePackagePath = ClassUtils.convertClassNameToResourcePath(basePackage);
        String resourcePattern = "**/*.class";
        String ex = "classpath*:" + basePackagePath + '/' + resourcePattern;
        try {
            return resourcePatternResolver.getResources(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据资源、根URL和扫描包获取类全名
     */
    private String getClassNameByResource(Resource resource, URL rootDirURL, String basePackage) {
        JarFile jarFile = null;
        boolean closeJarFile = false;
        JarEntry entry;
        try {
            URLConnection con = resource.getURL().openConnection();
            if (con instanceof JarURLConnection) {
                JarURLConnection entries = (JarURLConnection) con;
                ResourceUtils.useCachesIfNecessary(entries);
                jarFile = entries.getJarFile();
//                String jarFileUrl = entries.getJarFileURL().toExternalForm();
                entry = entries.getJarEntry();
                String rootEntryPath = entry != null ? entry.getName() : "";
                String classFullPath = rootEntryPath.substring(0, rootEntryPath.length() - ".class".length());
                String classFullName = ClassUtils.convertResourcePathToClassName(classFullPath);
//                System.out.println("jar包里面的类:"+classFullName);
                closeJarFile = !entries.getUseCaches();
                return classFullName;
            } else {
                String resourcePath = resource.getURL().getPath();
                String rootDirPath = rootDirURL.getPath();
                String path = resourcePath.substring(resourcePath.lastIndexOf(rootDirPath) + rootDirPath.length() - basePackage.length() - 1);
                String classFullPath = path.substring(0, path.length() - ".class".length());
                return ClassUtils.convertResourcePathToClassName(classFullPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (closeJarFile) {
                try {
                    if(jarFile != null) {
                        jarFile.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return "";
    }


    /**
     * 构建spring默认beanName
     *
     * @param className
     * @return
     */
    private String buildDefaultBeanName(String className) {
        String shortClassName = ClassUtils.getShortName(className);
        return Introspector.decapitalize(shortClassName);
    }

}
