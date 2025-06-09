package com.wuming.web.controller.front.vo;

import com.wuming.article.domain.BizPrize;


public class BizPrizeVo extends BizPrize {
    private String userName;
    private String schoolName;

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
}
