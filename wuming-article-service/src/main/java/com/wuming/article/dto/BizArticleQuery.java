package com.wuming.article.dto;

import com.wuming.article.domain.BizArticle;

import java.util.Date;
import java.util.List;

public class BizArticleQuery extends BizArticle {
    private List<Long> articleIds;
    private Date createBeginTime;
    private Date createEndTime;

    public List<Long> getArticleIds() {
        return articleIds;
    }

    public void setArticleIds(List<Long> articleIds) {
        this.articleIds = articleIds;
    }

    public Date getCreateBeginTime() {
        return createBeginTime;
    }

    public void setCreateBeginTime(Date createBeginTime) {
        this.createBeginTime = createBeginTime;
    }

    public Date getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(Date createEndTime) {
        this.createEndTime = createEndTime;
    }
}
