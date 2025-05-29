package com.wuming.common.oss.enums;

import software.amazon.awssdk.services.s3.model.BucketCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

/**
 * 桶访问策略配置
 *
 * @author 陈賝
 */
public enum AccessPolicyType {
    /**
     * private
     */
    PRIVATE("0", BucketCannedACL.PRIVATE, ObjectCannedACL.PRIVATE),

    /**
     * public
     */
    PUBLIC("1", BucketCannedACL.PUBLIC_READ_WRITE, ObjectCannedACL.PUBLIC_READ_WRITE),

    /**
     * custom
     */
    CUSTOM("2", BucketCannedACL.PUBLIC_READ, ObjectCannedACL.PUBLIC_READ);

    AccessPolicyType(String type, BucketCannedACL bucketCannedACL, ObjectCannedACL objectCannedACL) {
        this.type = type;
        this.bucketCannedACL = bucketCannedACL;
        this.objectCannedACL = objectCannedACL;
    }

    public String getType() {
        return type;
    }

    public BucketCannedACL getBucketCannedACL() {
        return bucketCannedACL;
    }

    public ObjectCannedACL getObjectCannedACL() {
        return objectCannedACL;
    }

    /**
     * 桶 权限类型（数据库值）
     */
    private final String type;

    /**
     * 桶 权限类型
     */
    private final BucketCannedACL bucketCannedACL;

    /**
     * 文件对象 权限类型
     */
    private final ObjectCannedACL objectCannedACL;

    public static AccessPolicyType getByType(String type) {
        for (AccessPolicyType value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("'type' not found By " + type);
    }

}
