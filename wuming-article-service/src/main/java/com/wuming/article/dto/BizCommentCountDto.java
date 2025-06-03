package com.wuming.article.dto;

import com.wuming.article.domain.BizComment;
import com.wuming.common.annotation.Excel;

import java.util.List;

public class BizCommentCountDto {
    private Long articleId;

    /** 评论类型（1点赞 2评论） */
    private String commentType;

    private Long count;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
