package com.wuming.common.oss.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS对象存储 配置属性
 *
 * @author Lion Li
 */
@Component
@ConfigurationProperties(prefix = "file-storage.s3")
public class OssProperties {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 访问站点
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String domain;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * ACCESS_KEY
     */
    private String accessKey;

    /**
     * SECRET_KEY
     */
    private String secretKey;

    /**
     * 存储空间名
     */
    private String bucketName;

    /**
     * 存储区域
     */
    private String region;

    /**
     * 是否https（Y=是,N=否）
     */
    private String isHttps;

    /**
     * 桶权限类型(0private 1public 2custom)
     */
    private String accessPolicy;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIsHttps() {
        return isHttps;
    }

    public void setIsHttps(String isHttps) {
        this.isHttps = isHttps;
    }

    public String getAccessPolicy() {
        return accessPolicy;
    }

    public void setAccessPolicy(String accessPolicy) {
        this.accessPolicy = accessPolicy;
    }
}
