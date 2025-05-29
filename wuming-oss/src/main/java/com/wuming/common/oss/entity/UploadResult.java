package com.wuming.common.oss.entity;

/**
 * 上传返回体
 *
 * @author Lion Li
 */

public class UploadResult {

    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 已上传对象的实体标记（用来校验文件）
     */
    private String eTag;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }
    public UploadResult builder(){
        return new UploadResult();
    }
}
