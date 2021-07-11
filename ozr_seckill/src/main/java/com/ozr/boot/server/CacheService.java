package com.ozr.boot.server;

/**
 * @Author OZR
 * @Date 2021/7/5 20:16
 */

/***
 * 封装本地缓存操作类
 *
 *
 */
public interface CacheService {
    //存方法
    void setCommonCache(String key,Object value);
    //取方法
    Object getFromCommonCache(String key);
}
