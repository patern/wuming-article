package com.wuming.article.dto;

import com.wuming.article.domain.BizComment;

import java.util.List;

public class BizCommentQuery extends BizComment {
    private List<Long> articleIds;

    public List<Long> getArticleIds() {
        return articleIds;
    }

    public void setArticleIds(List<Long> articleIds) {
        this.articleIds = articleIds;
    }
}
