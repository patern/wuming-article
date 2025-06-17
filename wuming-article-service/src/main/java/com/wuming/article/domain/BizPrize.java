package com.wuming.article.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.wuming.common.annotation.Excel;
import com.wuming.common.core.domain.BaseEntity;

/**
 * 用户提现对象 t_biz_prize
 * 
 * @author wuming
 * @date 2025-06-05
 */
public class BizPrize extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 提现编码 */
    private Long prizeId;

    /** 用户编码 */
    @Excel(name = "用户编码")
    private Long userId;

    /** 商户单号 */
    @Excel(name = "商户单号")
    private String prizeNo;

    /** 微信转账单号 */
    @Excel(name = "微信转账单号")
    private String transferBillNo;

    /** 提现金额 */
    @Excel(name = "提现金额")
    private BigDecimal money;

    /** 转账状态 */
    @Excel(name = "转账状态")
    private String status;

    private Set<String> statusSet;

    private Date createDateBegin;

    private Date createDateEnd;

    public void setPrizeId(Long prizeId) 
    {
        this.prizeId = prizeId;
    }

    public Long getPrizeId() 
    {
        return prizeId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setPrizeNo(String prizeNo) 
    {
        this.prizeNo = prizeNo;
    }

    public String getPrizeNo() 
    {
        return prizeNo;
    }

    public void setTransferBillNo(String transferBillNo) 
    {
        this.transferBillNo = transferBillNo;
    }

    public String getTransferBillNo() 
    {
        return transferBillNo;
    }

    public void setMoney(BigDecimal money) 
    {
        this.money = money;
    }

    public BigDecimal getMoney() 
    {
        return money;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public Set<String> getStatusSet() {
        return statusSet;
    }

    public void setStatusSet(Set<String> statusSet) {
        this.statusSet = statusSet;
    }

    public Date getCreateDateBegin() {
        return createDateBegin;
    }

    public void setCreateDateBegin(Date createDateBegin) {
        this.createDateBegin = createDateBegin;
    }

    public Date getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(Date createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("prizeId", getPrizeId())
            .append("userId", getUserId())
            .append("prizeNo", getPrizeNo())
            .append("transferBillNo", getTransferBillNo())
            .append("money", getMoney())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
