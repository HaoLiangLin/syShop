package com.jy2b.zxxfd.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.jy2b.zxxfd.contants.RedisConstants.CACHE_NULL_TTL;
import static com.jy2b.zxxfd.contants.RedisConstants.LOCK_SHOP_KEY;

public class RedisCache {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 将任意java对象序列化为json并存储在String类型的key中，并且可以设置TTL过期时间
     * @param key key
     * @param value 值
     * @param time 时间
     * @param timeUnit 时间单位
     */
    public void set(String key, Object value, Long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
    }

    /**
     * 将任意java对象序列化为json并存储在String类型的key中，并且可以设置逻辑过期时间，用于处理缓存击穿问题
     * @param key key
     * @param value 值
     * @param time 时间
     * @param timeUnit 时间单位
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit timeUnit) {
        // 设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));

        // 写入Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 根据指定的key查询缓存，并反序列化为指定类型，利用缓存空值的方式解决缓存穿透的问题
     *
     * Function<ID, R> dbFallback 函数式编程
     * ID 参数
     * R 返回值类型
     *
     * @param keyPrefix key的前缀
     * @param id id
     * @param type 真实的返回值类型
     * @param dbFallback 查询数据库的方法
     * @param time 时间
     * @param timeUnit 时间单位
     * @param <R> 定义返回值泛型
     * @param <ID> 定义id的泛型
     * @return R
     */
    public <R, ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit timeUnit) {
        String key = keyPrefix + id;
        // 1. 从Redis中查询缓存
        String keyJson = stringRedisTemplate.opsForValue().get(key);
        // 2. 判断是否存在
        if (StrUtil.isNotBlank(keyJson)) {
            // 3. 存在，直接返回
            return JSONUtil.toBean(keyJson, type);
        }
        // 判断命中的是否空值
        if (keyJson != null) {
            // 返回一个错误信息
            return null;
        }

        // 4.不存在，根据id查询数据库
        R r = dbFallback.apply(id);

        // 5. 不存在，返回错误
        if (r == null) {
            // 将空值写入Redis
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        // 6. 存在，写入Redis
        this.set(key, r, time, timeUnit);

        return r;
    }

    // 新建进程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 根据指定的key查询缓存，并反序列化为指定类型，利用逻辑过期解决缓存击穿问题
     *
     * @param keyPrefix key的前缀
     * @param id id
     * @param type 真实的返回值类型
     * @param dbFallback 查询数据库的方法
     * @param time 时间
     * @param timeUnit 时间单位
     * @param <T> 定义返回值泛型
     * @param <ID> 定义id的泛型
     * @return T
     */
    public <T, ID> T queryWithLogicalExpire(
            String keyPrefix, ID id, Class<T> type, Function<ID, T> dbFallback, Long time, TimeUnit timeUnit) {
        String key = keyPrefix + id;
        // 1. 从Redis中查询缓存
        String keyJson = stringRedisTemplate.opsForValue().get(key);
        // 2. 判断是否存在
        if (StrUtil.isBlank(keyJson)) {
            // 3. 存在，直接返回
            return null;
        }
        // 4. 命中，需要先把json序列化为对象
        RedisData redisData = JSONUtil.toBean(keyJson, RedisData.class);
        T t = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 5. 判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 未过期，直接返回
            return t;
        }
        // 已过期，需要缓存重建
        // 6. 缓存重建
        // 6.1. 获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        // 6.2. 判断是否获取锁成功
        if (isLock) {
            // 6.3. 成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                // 重建缓存
                try {
                    // 1. 先查数据库
                    T apply = dbFallback.apply(id);
                    // 写入Redis
                    this.setWithLogicalExpire(key, apply, time, timeUnit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // 释放锁
                    unLock(lockKey);
                }
            });
        }
        // 6.4 返回过期的信息
        return t;
    }

    // 尝试获取锁
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    // 释放锁
    private void unLock(String key) {
        stringRedisTemplate.delete(key);
    }
}
