package com.wuming.article.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.wuming.common.annotation.Excel;
import com.wuming.common.core.domain.BaseEntity;

/**
 * 文章对象 t_biz_article
 * 
 * @author patern
 * @date 2025-06-05
 */
public class BizArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 打卡编码 */
    private Long articleId;

    /** 打卡用户ID */
    @Excel(name = "打卡用户编码")
    private Long userId;

    /** 打卡标题 */
    @Excel(name = "打卡标题")
    private String articleTitle;

    /** 打卡类型（1视频 2语音 3文章） */
    @Excel(name = "打卡类型", readConverterExp = "1=视频,2=语音,3=文章")
    private String articleType;

    /** 打卡内容 */
    @Excel(name = "打卡内容")
    private String articleContent;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 打卡附件连接 */
    @Excel(name = "打卡附件连接")
    private String articleAttaUrl;

    /** 链接有效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "链接有效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invalidDate;

    /** 奖励金额 */
    @Excel(name = "奖励金额")
    private BigDecimal prize;

    /** 打卡状态（0正常 1关闭 2下架） */
    @Excel(name = "打卡状态", readConverterExp = "0=正常,1=关闭,2=下架")
    private String status;

    public void setArticleId(Long articleId) 
    {
        this.articleId = articleId;
    }

    public Long getArticleId() 
    {
        return articleId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
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

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setArticleAttaUrl(String articleAttaUrl) 
    {
        this.articleAttaUrl = articleAttaUrl;
    }

    public String getArticleAttaUrl() 
    {
        return articleAttaUrl;
    }

    public void setInvalidDate(Date invalidDate) 
    {
        this.invalidDate = invalidDate;
    }

    public Date getInvalidDate() 
    {
        return invalidDate;
    }

    public void setPrize(BigDecimal prize) 
    {
        this.prize = prize;
    }

    public BigDecimal getPrize() 
    {
        return prize;
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
            .append("fileName", getFileName())
            .append("articleAttaUrl", getArticleAttaUrl())
            .append("invalidDate", getInvalidDate())
            .append("prize", getPrize())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
