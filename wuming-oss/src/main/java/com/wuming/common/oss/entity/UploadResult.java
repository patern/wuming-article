package com.wuming.common.oss.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

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
    private String fileName;

    /**
     * 文件名
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invalidDate;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(Date invalidDate) {
        this.invalidDate = invalidDate;
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
