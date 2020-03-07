package cn.lanyue.cas.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author 
 * @Description cache properties
 * @Date 2020/2/17 14:29
 */
@Data
@ConfigurationProperties(prefix = "caffeine")
public class CaffeineProperties {

    private List<Config> configs;

    /**
     * #  initialCapacity=[integer]: 初始的缓存空间大小
     * #  maximumSize=[long]: 缓存的最大条数
     * #  maximumWeight=[long]: 缓存的最大权重
     * #  expireAfterAccess=[duration]: 最后一次写入或访问后经过固定时间过期
     * #  expireAfterWrite=[duration]: 最后一次写入后经过固定时间过期
     * #  refreshAfterWrite=[duration]: 创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
     * #  weakKeys: 打开key的弱引用
     * #  weakValues：打开value的弱引用
     * #  softValues：打开value的软引用
     * #  recordStats：开发统计功能
     */
    @Data
    public static class Config {
        //缓存名称
        private String name;
        //初始化大小
        private int initialCapacity;

        private long maximumSize;

        //private long maximumWeight;

        private long expireAfterWriteDuration;

        //private long expireAfterAccessDuration;

        //private long refreshDuration;

        //private int concurrencyLevel;
    }
}

