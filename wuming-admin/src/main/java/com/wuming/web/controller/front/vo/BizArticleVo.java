package com.wuming.web.controller.front.vo;

import com.wuming.article.domain.BizArticle;
import com.wuming.article.domain.BizComment;

import java.util.List;

public class BizArticleVo extends BizArticle {
    private String userName;
    private String schoolName;
    private List<BizComment> comments;
    private List<BizComment> upVotes;
    private Long commentCounts;
    private Long upvoteCount;

    private Boolean upvote;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public List<BizComment> getComments() {
        return comments;
    }

    public void setComments(List<BizComment> comments) {
        this.comments = comments;
    }

    public List<BizComment> getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(List<BizComment> upVotes) {
        this.upVotes = upVotes;
    }

    public Long getCommentCounts() {
        return commentCounts;
    }

    public void setCommentCounts(Long commentCounts) {
        this.commentCounts = commentCounts;
    }

    public Long getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(Long upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public Boolean getUpvote() {
        return upvote;
    }

    public void setUpvote(Boolean upvote) {
        this.upvote = upvote;
    }
}
