package com.dili.ss.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * redicache 工具类
 *
 */
@SuppressWarnings("unchecked")
@Component
@ConditionalOnExpression("'${redis.enable}'=='true'")
public class RedisUtil {
    @SuppressWarnings("rawtypes")
    @Autowired
    protected RedisTemplate redisTemplate;

    /**
     * 集合、列表、Set等对象自行取redisTemplate操作
     * @return
     */
    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }
    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
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
    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public <T> T get(final String key, Class<T> clazz) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if(null != result && result.getClass().isAssignableFrom(clazz)){
            return (T)result;
        }
        return result == null ? null : JSON.parseObject(result.toString(), clazz);
    }

    /**
     * 根据key自增value
     * @param key
     * @param value
     * @return
     */
    public long increment(String key, Long value){
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.increment(key, value);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime 过期时间，单位秒
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        return set(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit 过期时间单位枚举
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, expireTime, timeUnit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return 是否获取成功
     */
    public boolean setIfAbsent(final String key, Object value) {
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            return operations.setIfAbsent(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime 过期时间，单位秒
     * @return 是否获取成功
     */
    public boolean setIfAbsent(final String key, Object value, Long expireTime) {
        return setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit 过期时间单位枚举
     * @return 是否获取成功
     */
    public boolean setIfAbsent(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            return operations.setIfAbsent(key, value, expireTime, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 推后过期时间
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit timeUnit){
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 推后过期时间到指定日期
     * @param key
     * @param date
     * @return
     */
    public Boolean expireAt(String key, Date date){
        return redisTemplate.expireAt(key, date);
    }
}
