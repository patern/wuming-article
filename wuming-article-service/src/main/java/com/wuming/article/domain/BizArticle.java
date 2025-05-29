package com.wuming.article.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.wuming.common.annotation.Excel;
import com.wuming.common.core.domain.BaseEntity;

/**
 * 打卡对象 t_biz_article
 * 
 * @author patern
 * @date 2025-05-27
 */
public class BizArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 打卡ID */
    private Long articleId;

    /** 用户ID */
    private Long userId;

    /** 公告标题 */
    @Excel(name = "公告标题")
    private String articleTitle;

    /** 公告类型（1视频 2语音 3打卡） */
    @Excel(name = "公告类型", readConverterExp = "1=视频,2=语音,3=打卡")
    private String articleType;

    /** 公告内容 */
    @Excel(name = "公告内容")
    private String articleContent;

    /** 打卡链接 */
    @Excel(name = "打卡链接")
    private String articleAttaUrl;

    /** 公告状态（0正常 1关闭 2下架） */
    @Excel(name = "公告状态", readConverterExp = "0=正常,1=关闭,2=下架")
    private String status;

    public void setArticleId(Long articleId) 
    {
        this.articleId = articleId;
    }

    public Long getArticleId() 
    {
        return articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setArticleTitle(String articleTitle)
    {
        this.articleTitle = articleTitle;
    }

    public String getArticleTitle() 
    {
        return articleTitle;
    }

    public void setArticleType(String articleType) 
    {
        this.articleType = articleType;
    }

    public String getArticleType() 
    {
        return articleType;
    }

    public void setArticleContent(String articleContent) 
    {
        this.articleContent = articleContent;
    }

    public String getArticleContent() 
    {
        return articleContent;
    }

    public void setArticleAttaUrl(String articleAttaUrl) 
    {
        this.articleAttaUrl = articleAttaUrl;
    }

    public String getArticleAttaUrl() 
    {
        return articleAttaUrl;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("articleId", getArticleId())
            .append("userId", getUserId())
            .append("articleTitle", getArticleTitle())
            .append("articleType", getArticleType())
            .append("articleContent", getArticleContent())
            .append("articleAttaUrl", getArticleAttaUrl())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
