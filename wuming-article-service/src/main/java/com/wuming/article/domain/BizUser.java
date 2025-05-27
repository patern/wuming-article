package com.wuming.article.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.wuming.common.annotation.Excel;
import com.wuming.common.core.domain.BaseEntity;

/**
 * 用户对象 t_biz_user
 * 
 * @author patern
 * @date 2025-05-27
 */
public class BizUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long userId;

    /** 学校名称 */
    @Excel(name = "学校名称")
    private String schoolName;

    /** 微信ID */
    @Excel(name = "微信ID")
    private String openId;

    /** 用户真实名称 */
    @Excel(name = "用户真实名称")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 性别（0女 1男） */
    @Excel(name = "性别", readConverterExp = "0=女,1=男")
    private String sex;

    /** 评论状态（0正常 1关闭） */
    @Excel(name = "评论状态", readConverterExp = "0=正常,1=关闭")
    private String status;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("schoolName", getSchoolName())
            .append("openId", getOpenId())
            .append("userName", getUserName())
            .append("nickName", getNickName())
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
