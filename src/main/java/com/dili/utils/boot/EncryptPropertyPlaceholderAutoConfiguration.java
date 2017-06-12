package com.dili.utils.boot;


import com.dili.utils.SpringUtil;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.spring31.properties.EncryptablePropertySourcesPlaceholderConfigurer;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;

/**
 * 由于spring boot中@Value注解无法获取${xxx}形式的变量，所以只能在Application.java上使用注解:
 * @EnableEncryptableProperties
 * @PropertySource(name="EncryptedProperties", value = "classpath:security.properties")
 * 并配置application.properties属性jasypt.encryptor.password=security(密码)
 * Created by asiam on 2017/4/6 0006.
 */
//@Configuration
//@ConditionalOnClass({StandardPBEByteEncryptor.class, EncryptablePropertySourcesPlaceholderConfigurer.class})
//@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class EncryptPropertyPlaceholderAutoConfiguration {

    private static final String SECURITY_PROPERTIES_FILE = "security.properties";
    public static final String PASSWORD = "security";

//    @Bean
//    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
        encryptor.setPassword(PASSWORD);
        EncryptablePropertySourcesPlaceholderConfigurer
                configurer = new EncryptablePropertySourcesPlaceholderConfigurer(encryptor);
        configurer.setLocation(new ClassPathResource(SECURITY_PROPERTIES_FILE));
        return configurer;
    }

    /**
     * 根据code加密，返回并打印加密后的串
     * @return
     */
    @Test
    public void encrypt(){
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(PASSWORD);
        String encrypted = encryptor.encrypt("123456");
        System.out.println(encrypted);
    }
}