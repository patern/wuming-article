package com.wuming.article.dto;


import java.math.BigDecimal;

public class BizArticleCountDto {
    private Long userId;

    private String articleType;

    private Long allCount;

    private Long videoCount;

    private Long voiceCount;

    private Long articleCount;

    private Long activeCount;

    private Long inActiveCount;

    private Long delCount;

    private String status;

    private String schoolName;

    private String userName;

    private String nickName;

    private String telephone;

    private BigDecimal totalPrize;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public Long getAllCount() {
        return allCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public Long getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(Long videoCount) {
        this.videoCount = videoCount;
    }

    public Long getVoiceCount() {
        return voiceCount;
    }

    public void setVoiceCount(Long voiceCount) {
        this.voiceCount = voiceCount;
    }

    public Long getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Long articleCount) {
        this.articleCount = articleCount;
    }

    public Long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Long activeCount) {
        this.activeCount = activeCount;
    }

    public Long getInActiveCount() {
        return inActiveCount;
    }

    public void setInActiveCount(Long inActiveCount) {
        this.inActiveCount = inActiveCount;
    }

    public Long getDelCount() {
        return delCount;
    }

    public void setDelCount(Long delCount) {
        this.delCount = delCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BigDecimal getTotalPrize() {
        return totalPrize;
    }

    public void setTotalPrize(BigDecimal totalPrize) {
        this.totalPrize = totalPrize;
    }
}
