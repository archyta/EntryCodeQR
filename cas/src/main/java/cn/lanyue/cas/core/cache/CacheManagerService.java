package cn.lanyue.cas.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author 
 * @Description 缓存管理器服务
 * @Date 2020/2/17 16:41
 */
@Service
public class CacheManagerService {

    @Autowired
    CacheManager cacheManager;


    /**
     * 保存或更新缓存
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     */
    public void put(CacheName cacheName, String key, Object value) {
        CaffeineCache cache = getCache(cacheName);
        cache.put(key, value);
    }

    /**
     * 删除缓存
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @return 是否删除成功
     */
    public boolean remove(CacheName cacheName, String key) {
        CaffeineCache cache = getCache(cacheName);
        return cache.evictIfPresent(key);
    }

    /**
     * 获取缓存
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(CacheName cacheName, String key) {
        CaffeineCache cache = getCache(cacheName);
        if (cache.get(key) != null) {
            Object o = cache.get(key).get();
            return o;
        }
        return null;
    }


    private CaffeineCache getCache(CacheName cacheName) {
        CaffeineCache cache = (CaffeineCache)cacheManager.getCache(cacheName.name());
        if (cache == null) {
            throw new RuntimeException("获取缓存管理器失败");
        }
        return cache;
    }

}
