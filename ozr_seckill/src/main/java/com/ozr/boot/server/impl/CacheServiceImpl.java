package com.ozr.boot.server.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ozr.boot.server.CacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


/**
 * @Author OZR
 * @Date 2021/7/5 20:18
 */
@Service
public class CacheServiceImpl implements CacheService {
    private Cache<String,Object> commonCache = null;
    @PostConstruct
    public void init(){
        commonCache = CacheBuilder.newBuilder()
                .initialCapacity(10)// //初始容量，放10个对象（10个key）
                .maximumSize(100) //最大的容量  超过使用lru策越移除缓存
               // .expireAfterAccess(60, TimeUnit.SECONDS) //相对于被访问后的60s过期
               .expireAfterWrite(30,TimeUnit.SECONDS)//相对于写入缓存后的60s过期
                .build();
    }
    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key,value);
    }
    @Override
    public Object getFromCommonCache(String key) {
        //不存在就返回null
        return commonCache.getIfPresent(key);
    }
}
