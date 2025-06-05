package com.wuming.article.pay;

import com.alibaba.fastjson2.JSONObject;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.wuming.common.constant.CacheConstants;
import com.wuming.common.core.redis.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class WxPayFactory {

    private final Map<String, WxPayService> CLIENT_CACHE = new ConcurrentHashMap<>();
    private final ReentrantLock LOCK = new ReentrantLock();
    @Autowired
    private RedisCache redisCache;
    private WxPay properties;

    private WxPayService client;

    /**
     * 获取默认实例
     */
    public WxPayService instance() {
        String payConfig = redisCache.getCacheObject(CacheConstants.WX_PAY_CONFIG);
        // 获取redis 默认类型
        if (StringUtils.isEmpty(payConfig)) {
            throw  new RuntimeException("未获取到支付配置");
        }
        return instance(payConfig);
    }

    public WxPay getProperties() {
        return properties;
    }

    /**
     * 根据类型获取实例
     */
    public WxPayService instance(String configKey) {
        properties = JSONObject.parseObject(configKey, WxPay.class);
        String key = "wxPay";
        // 客户端不存在或配置不相同则重新构建
        LOCK.lock();
        try {
            client = CLIENT_CACHE.get(key);
            if (client == null) {
                CLIENT_CACHE.put(key, getWxService(properties));
//                    log.info("创建OSS实例 key => {}", configKey);
                return CLIENT_CACHE.get(key);
            }
        } finally {
            LOCK.unlock();
        }
        return client;
    }

    public WxPayService getWxService(WxPay wxPay) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(wxPay.getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(wxPay.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(wxPay.getMchKey()));
        payConfig.setSubAppId(StringUtils.trimToNull(wxPay.getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(wxPay.getSubMchId()));
        payConfig.setKeyPath(StringUtils.trimToNull(wxPay.getKeyPath()));
        payConfig.setApiV3Key(StringUtils.trimToNull(wxPay.getApiV3Key()));
        payConfig.setCertSerialNo(StringUtils.trimToNull(wxPay.getCertSerialNo()));
        payConfig.setPrivateKeyPath(StringUtils.trimToNull(wxPay.getPrivateKeyPath()));
        payConfig.setPrivateCertPath(StringUtils.trimToNull(wxPay.getPrivateCertPath()));
        payConfig.setNotifyUrl(StringUtils.trimToNull(wxPay.getUrl())
                + StringUtils.trimToNull(wxPay.getNotifyUrl()));

        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
}