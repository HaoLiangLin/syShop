package com.jy2b.zxxfd.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.jy2b.zxxfd.contants.RedisConstants.*;

/**
 * @author 林武泰
 * 缓存工具类
 */
@Component
public class RedisUtils {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 使用JSON字符串格式对资源进行缓存
     * @param key 唯一键
     * @param value 值
     * @param time 有效时间
     * @param timeUnit 时间单位
     * @param <T> 定义泛型
     */
    public <T> void setStr(@NonNull String key, @NonNull T value, Long time, TimeUnit timeUnit) {
        if (time != null) {
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
            return;
        }
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));
    }

    /**
     * 获取String类型缓存，将值返回指定类型
     * @param key 唯一键
     * @param type 返回类型
     * @param <T> 声明类型
     * @return T
     */
    public <T> T getStrForBean(String key, Class<T> type) {
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(jsonStr)) {
            return null;
        }
        return JSONUtil.toBean(jsonStr, type);
    }

    /**
     * 获取String类型缓存，将值返回指定类型的集合
     * @param key 唯一键
     * @param type 返回集合中元素类型
     * @param <T> 类型声明
     * @return List<T>
     */
    public <T> List<T> getStrForList(String key, Class<T> type) {
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSONUtil.toList(jsonStr, type);
    }

    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 查询并防止缓存穿透
     * @param keyPrefix key前缀
     * @param id id
     * @param type 返回类型
     * @param dbFallback 查询函数
     * @param time 缓存时间
     * @param timeUnit 时间单位
     * @param <ID> 声明泛型 id
     * @param <R> 声明泛型 返回类型
     * @return R
     */
    public <ID, R> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit timeUnit
    ) {
        // 生成key
        String cacheKey = keyPrefix + ":" + id;
        // 从Redis中查询
        String cacheResult = stringRedisTemplate.opsForValue().get(cacheKey);
        // 判断是否不为空
        if (StrUtil.isNotBlank(cacheResult)) {
            return JSONUtil.toBean(cacheResult, type);
        }

        // 查询数据库
        R r = dbFallback.apply(id);
        // 判断是否为空
        if (r == null) {
            // 将空值存入Redis
            stringRedisTemplate.opsForValue().set(cacheKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            // 返回Null
            return null;
        }
        // 写入Redis
        this.setStr(cacheKey, r, time, timeUnit);
        return r;
    }

    /**
     * 回收资源回收
     * @param resource 要回收的资源
     * @param time 回收保存时间
     * @param timeUnit 回收保存时间单位
     * @param <T> 泛型定义
     */
    public <T> void setRecycleBin(@NonNull T resource, @NonNull String name, Long time, TimeUnit timeUnit) {
        // 获取类名
        String canonicalName = resource.getClass().getCanonicalName();
        // 获取放入回收站的key
        String key = RECYCLE_BIN_KEY + canonicalName + ":" + name;

        // 判断存入时间是否为空
        if (Objects.isNull(time)) {
            time = RECYCLE_BIN_TTL;
        }
        // 判断存入时间单位是否为空
        if (timeUnit == null) {
            // 设置时间默认为天
            timeUnit = TimeUnit.DAYS;
        }

        // 将回收资源转为json字符串
        String jsonStr = JSONUtil.toJsonStr(resource);
        // 将回收资源存入Redis
        stringRedisTemplate.opsForValue().set(key, jsonStr, time, timeUnit);
    }

    /**
     * 回收资源找回
     * @param type 回收资源类型
     * @param <T> 定义泛型
     * @return T
     */
    public <T> T getRecycleBin(@NonNull Class<T> type, @NonNull String name) {
        // 获取类名
        String canonicalName = type.getCanonicalName();
        // 获取要进行回收资源找回的key
        String key = RECYCLE_BIN_KEY + canonicalName + ":" + name;
        // 从Redis中获取资源
        String jsonStr = stringRedisTemplate.opsForValue().get(key);

        // 判断资源是否为空
        if (StrUtil.isBlank(jsonStr)) {
            return null;
        }

        // 将json字符串转换为bean
        return JSONUtil.toBean(jsonStr, type);
    }

    /**
     * 回收资源删除
     * @param type 回收资源类型
     * @param name 资源名称
     * @param <T> 定义泛型
     * @return Boolean
     */
    public <T> Boolean removeRecycleBin(@NonNull Class<T> type, @NonNull String name) {
        // 获取类名
        String canonicalName = type.getCanonicalName();
        // 获取要进行回收资源找回的key
        String key = RECYCLE_BIN_KEY + canonicalName + ":" + name;

        // 删除回收站资源
        return stringRedisTemplate.delete(key);
    }

    /**
     * 根据资源类型查询回收站
     * @param type 资源类型
     * @param <T> 定义泛型
     * @return Set<String>
     */
    public <T> Set<String> selectRecycleBin(@NonNull Class<T> type) {
        // 获取类名
        String canonicalName = type.getCanonicalName();
        // 获取资源key
        String key = RECYCLE_BIN_KEY + canonicalName + ":*";

        // 查询相关回收资源
        Set<String> keys = stringRedisTemplate.keys(key);
        if (keys == null) {
            return null;
        }

        // 创建新的set集合
        Set<String> names = new HashSet<>();
        // 循环拿到的所有key
        keys.forEach(val -> {
            // 根据冒号对key进行分割
            String[] split = val.split(":");
            // 将最后一个名字存入set集合
            names.add(split[split.length - 1]);
        });

        return names;
    }
}
