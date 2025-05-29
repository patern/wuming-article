package com.wuming.common.oss.factory;

import com.wuming.common.core.redis.RedisCache;
import com.wuming.common.oss.constant.OssConstant;
import com.wuming.common.oss.core.OssClient;
import com.wuming.common.oss.exception.OssException;
import com.wuming.common.oss.properties.OssProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 文件上传Factory
 *
 * @author Lion Li
 */
@Component
public class OssFactory {

    private final Map<String, OssClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private final ReentrantLock LOCK = new ReentrantLock();
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private OssProperties properties;
    /**
     * 获取默认实例
     */
    public OssClient instance() {
        // 获取redis 默认类型
        String configKey = redisCache.getCacheObject(OssConstant.DEFAULT_CONFIG_KEY);
        if (StringUtils.isEmpty(configKey)) {
            throw new OssException("文件存储服务类型无法找到!");
        }
        return instance(configKey);
    }

    /**
     * 根据类型获取实例
     */
    public OssClient instance(String configKey) {
        // 使用租户标识避免多个租户相同key实例覆盖
        String key = configKey;
        if (StringUtils.isNotBlank(properties.getTenantId())) {
            key = properties.getTenantId() + ":" + configKey;
        }
        OssClient client = CLIENT_CACHE.get(key);
        // 客户端不存在或配置不相同则重新构建
        if (client == null || !client.checkPropertiesSame(properties)) {
            LOCK.lock();
            try {
                client = CLIENT_CACHE.get(key);
                if (client == null || !client.checkPropertiesSame(properties)) {
                    CLIENT_CACHE.put(key, new OssClient(configKey, properties));
//                    log.info("创建OSS实例 key => {}", configKey);
                    return CLIENT_CACHE.get(key);
                }
            } finally {
                LOCK.unlock();
            }
        }
        return client;
    }

}
