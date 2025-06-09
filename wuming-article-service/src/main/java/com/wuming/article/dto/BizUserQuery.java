package com.wuming.article.dto;

import com.wuming.article.domain.BizComment;
import com.wuming.article.domain.BizUser;

import java.util.List;
import java.util.Set;

public class BizUserQuery extends BizUser {
    private Set<Long> userIds;

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }
}
