package com.wuming.article.service;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsCancelResult;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsGetResult;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.wuming.article.domain.BizUser;
import com.wuming.article.dto.BizUserQuery;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 打卡用户Service接口
 *
 * @author wuming
 * @date 2025-05-28
 */
public interface IWxService {
    /**
     * 查询打卡用户openId
     *
     * @param jsCode 打卡用户主键
     * @return 打卡用户
     */
    public String getUserOpenId(String jsCode);


    /**
     * 新增打卡用户
     *
     * @param userId 打卡用户
     * @param money 支付金额
     * @return 结果
     */
    public TransferBillsResult payPrize(Long userId, BigDecimal money) throws WxPayException;

    TransferBillsCancelResult cancelTransfer( String outBillNo) throws WxPayException;

    TransferBillsGetResult getTransferOrder( String outBillNo) throws WxPayException;

    String payTransferNotify(SignatureHeader header, String notifyData);
}
