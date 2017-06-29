package com.dili.ss.metadata;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 值对管理器<br>
 * 只有注册才能查找,其实是值对的一个管理器
 *
 * @author WangMi
 * @create 2017-05-29
 */
public class ValuePairManager {
    // 缓冲
    private static final Map<String, List<ValuePair<?>>> cache = new ConcurrentHashMap<String, List<ValuePair<?>>>();

    /**
     * 注册值对
     *
     * @param key
     *          主键
     * @param value
     *          值
     */
    public static void register(String key, List<ValuePair<?>> value) {
        cache.put(key, value);
    }

    /**
     * 查找
     *
     * @param key
     * @return
     */
    public static List<ValuePair<?>> get(String key) {
        return cache.get(key);
    }

    /**
     * 清理
     */
    public static void clearValuePairs() {
        cache.clear();
    }
}
