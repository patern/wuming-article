package com.wuming.article.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.wuming.common.annotation.Excel;
import com.wuming.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 打卡用户对象 t_biz_user
 * 
 * @author wuming
 * @date 2025-05-28
 */
public class BizUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户编码 */
    private Long userId;

    /** 学校名称 */
    @Excel(name = "学校名称")
    private String schoolName;

    /** 微信ID */
    @Excel(name = "微信ID")
    private String openId;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String telephone;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 性别（0女 1男） */
    @Excel(name = "性别", readConverterExp = "0=女,1=男")
    private String sex;

    /** 评论状态（0正常 1关闭） */
    @Excel(name = "评论状态", readConverterExp = "0=正常,1=关闭")
    private String status;

    private String avatarUrl;
    /**
     * token信息
     */
    private String token;
    private BigDecimal prizeTotal;
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setSchoolName(String schoolName) 
    {
        this.schoolName = schoolName;
    }

    public String getSchoolName() 
    {
        return schoolName;
    }

    public void setOpenId(String openId) 
    {
        this.openId = openId;
    }

    public String getOpenId() 
    {
        return openId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setNickName(String nickName) 
    {
        this.nickName = nickName;
    }

    public String getNickName() 
    {
        return nickName;
    }

    public void setTelephone(String telephone) 
    {
        this.telephone = telephone;
    }

    public String getTelephone() 
    {
        return telephone;
    }

    public void setIdCard(String idCard) 
    {
        this.idCard = idCard;
    }

    public String getIdCard() 
    {
        return idCard;
    }

    public void setSex(String sex) 
    {
        this.sex = sex;
    }

    public String getSex() 
    {
        return sex;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BigDecimal getPrizeTotal() {
        return prizeTotal;
    }

    public void setPrizeTotal(BigDecimal prizeTotal) {
        this.prizeTotal = prizeTotal;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("schoolName", getSchoolName())
            .append("openId", getOpenId())
            .append("userName", getUserName())
            .append("nickName", getNickName())
            .append("telephone", getTelephone())
            .append("idCard", getIdCard())
            .append("sex", getSex())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
