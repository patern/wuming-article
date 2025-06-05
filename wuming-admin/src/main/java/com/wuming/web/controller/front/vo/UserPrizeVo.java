package com.wuming.web.controller.front.vo;

import com.wuming.article.domain.BizArticle;
import com.wuming.article.domain.BizComment;

import java.math.BigDecimal;
import java.util.List;

public class UserPrizeVo {
    private Long userId;
    private Long articleId;
    private BigDecimal prize;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public BigDecimal getPrize() {
        return prize;
    }

    public void setPrize(BigDecimal prize) {
        this.prize = prize;
    }
}
