package com.wuming.article.dto;

import com.wuming.article.domain.BizComment;
import com.wuming.article.domain.BizUser;

import java.util.List;

public class BizUserQuery extends BizUser {
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
