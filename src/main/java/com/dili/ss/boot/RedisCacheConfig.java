package com.dili.ss.boot;

/**
 * Created by asiamastor on 2017/1/3.
 */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


/**
 * redis 缓存配置;
 *
 * 注意：RedisCacheConfig这里也可以不用继承 ：CachingConfigurerSupport，也就是直接一个普通的Class就好了；
 *
 * 这里主要我们之后要重新实现 key的生成策略，只要这里修改KeyGenerator，其它位置不用修改就生效了。
 *
 * 普通使用普通类的方式的话，那么在使用@Cacheable的时候还需要指定KeyGenerator的名称;这样编码的时候比较麻烦。
 *
 * @author asiamastor
 */

@Configuration
@EnableCaching //启用缓存，这个注解很重要
@ConditionalOnExpression("'${redis.enable}'=='true'")
public class RedisCacheConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String hostName;
    @Value("${spring.redis.port}")
    private Integer port;
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        // Defaults
        redisConnectionFactory.setHostName(hostName);
        redisConnectionFactory.setPort(port);
        return redisConnectionFactory;
    }

    /**
     * 缓存管理器.     * @param redisTemplate
     *
     * @return
     */
//    @Bean
//    public CacheManager cacheManager(@Qualifier("redisTemplate") RedisTemplate<?, ?> redisTemplate) {
//        CacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        return cacheManager;
//    }
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory);
        Set<String> cacheNames = new HashSet<String>() {{
            add("codeNameCache");
        }};
        builder.initialCacheNames(cacheNames);
        return builder.build();
    }


    /**
     * KEY生成器用法:
     * @Cacheable(value = "usercache",keyGenerator = "wiselyKeyGenerator")
     * @return
     */
    @Bean
    public KeyGenerator wiselyKeyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };

    }

    /**
     * redis模板操作类,类似于jdbcTemplate的一个类;
     * <p>
     * 虽然CacheManager也能获取到Cache对象，但是操作起来没有那么灵活；
     * <p>
     * 这里在扩展下：RedisTemplate这个类不见得很好操作，我们可以在进行扩展一个我们
     * <p>
     * 自己的缓存类，比如：RedisStorage类;
     *
     * @param redisConnectionFactory : 通过Spring进行注入，参数在application.properties进行配置；
     * @return
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //key序列化方式;（不然会出现乱码;）,但是如果方法上有Long等非String类型的话，会报类型转换错误；
        //所以在没有自己定义key生成策略的时候，以下这个代码建议不要这么写，可以不配置或者自己实现ObjectRedisSerializer
        //或者JdkSerializationRedisSerializer序列化方式;
//           RedisSerializer<String>redisSerializer = new StringRedisSerializer();//Long类型不可以会出现异常信息;
//           redisTemplate.setKeySerializer(redisSerializer);
//           redisTemplate.setHashKeySerializer(redisSerializer);

        StringRedisSerializer stringRedisSerializer= new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}

