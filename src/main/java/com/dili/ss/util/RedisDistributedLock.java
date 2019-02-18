package com.dili.ss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 * @author asiamaster
 * @since 2019/2/1
 **/
@Component
@ConditionalOnExpression("'${redis.enable}'=='true'")
public class RedisDistributedLock{

    private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String UNLOCK_LUA;

    static {
//        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    public Object get(String key) {
        //下面的代码有bug，字符串两边会有双引号
//        try {
//            RedisCallback<String> callback = (connection) -> {
//                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
//                return commands.get(key);
//            };
//            return redisTemplate.execute(callback);
//        } catch (Exception e) {
//            logger.error("get redis occured an exception", e);
//            return null;
//        }
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取锁
     * @param key
     * @param value
     * @param expire 单位秒
     * @return  获取失败返回false
     */
    public boolean tryGetLock(String key, String value,  long expire) {
//        try {
//            RedisCallback<String> callback = (connection) -> {
//                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
//                return commands.set(key, value, "NX", "PX", expire);
//            };
//            String result = redisTemplate.execute(callback);
//            return !StringUtils.isEmpty(result);
//        } catch (Exception e) {
//            logger.error("set redis occured an exception", e);
//        }
//        return false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            return operations.setIfAbsent(key, value, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean releaseLock(String key,String requestId) {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            //由于在RedisCacheConfig中配置了redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            //所以redis中的值在两边有引号，对比值时需要在两边加上引号
            final String value = "\""+requestId+"\"";
            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            RedisCallback<Long> callback = (connection) -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, Collections.singletonList(key), Collections.singletonList(value));
                }
                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, Collections.singletonList(key), Collections.singletonList(value));
                }
                return 0L;
            };
            Long result = (Long)redisTemplate.execute(callback);
            return result != null && result > 0;
        } catch (Exception e) {
            logger.error("release lock occured an exception", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            //lockFlag.remove();
        }
        return false;
    }

}
