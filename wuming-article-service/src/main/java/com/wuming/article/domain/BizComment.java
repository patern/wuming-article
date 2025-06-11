package com.wuming.article.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.wuming.common.annotation.Excel;
import com.wuming.common.core.domain.BaseEntity;

/**
 * 打卡评论对象 t_biz_comment
 * 
 * @author patern
 * @date 2025-05-27
 */
public class BizComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 打卡ID */
    @Excel(name = "打卡ID")
    private Long articleId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;
    /** 用户ID */
    @Excel(name = "用户名")
    private String userName;
    /** 公告标题 */
    @Excel(name = "公告标题")
    private String commentContent;

    /** 评论类型（1点赞 2评论） */
    @Excel(name = "评论类型", readConverterExp = "1=点赞,2=评论")
    private String commentType;

    /** 评论状态（0正常 1关闭） */
    @Excel(name = "评论状态", readConverterExp = "0=正常,1=关闭")
    private String status;

    public void setCommentId(Long commentId) 
    {
        this.commentId = commentId;
    }

    public Long getCommentId() 
    {
        return commentId;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCommentContent(String commentContent)
    {
        this.commentContent = commentContent;
    }

    public String getCommentContent() 
    {
        return commentContent;
    }

    public void setCommentType(String commentType) 
    {
        this.commentType = commentType;
    }

    public String getCommentType() 
    {
        return commentType;
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
            .append("commentId", getCommentId())
            .append("articleId", getArticleId())
            .append("userId", getUserId())
            .append("commentContent", getCommentContent())
            .append("commentType", getCommentType())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
