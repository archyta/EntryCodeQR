package cn.lanyue.cas.config;

import cn.lanyue.cas.config.properties.CaffeineProperties;
import cn.lanyue.cas.utils.Validator;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/17 15:24
 */
@EnableCaching
@Configuration
@EnableConfigurationProperties(CaffeineProperties.class)
public class CacheConfig {

    public static final int DEFAULT_MAXSIZE = 5000;
    public static final int DEFAULT_DURATION = 60;


    private CaffeineProperties caffeineProperties;

    List<CaffeineProperties.Config> configs = Lists.newArrayList();

    @Autowired
    public CacheConfig(CaffeineProperties caffeineProperties) {
        this.caffeineProperties = caffeineProperties;
    }

    @PostConstruct
    public void init() {
        configs = this.caffeineProperties.getConfigs();
        if (Validator.isNullOrEmpty(configs)) {
            throw new RuntimeException("初始化缓存参数失败");
        }
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        ArrayList<CaffeineCache> caches = new ArrayList<>();

        for (CaffeineProperties.Config c : configs) {
            if (c.getExpireAfterWriteDuration() <= 0)  c.setExpireAfterWriteDuration(DEFAULT_DURATION);
            if (c.getMaximumSize() <= 0) c.setMaximumSize(DEFAULT_MAXSIZE);
            if (c.getInitialCapacity() <= 0) c.setInitialCapacity(0);
            caches.add(
                    new CaffeineCache(
                            c.getName(),
                            Caffeine.newBuilder().recordStats()
                                    .expireAfterWrite(c.getExpireAfterWriteDuration(), TimeUnit.SECONDS)
                                    .maximumSize(c.getMaximumSize())
                                    .initialCapacity(c.getInitialCapacity())
                                    .build()
                    )
            );

        }

        cacheManager.setCaches(caches);
        return cacheManager;
    }


}
