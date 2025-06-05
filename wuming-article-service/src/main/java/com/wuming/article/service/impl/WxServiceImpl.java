package com.wuming.article.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Response;
import com.github.binarywang.wxpay.bean.transfer.*;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.wuming.article.pay.WxPayFactory;
import com.wuming.article.service.IWxService;
import com.wuming.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service

public class WxServiceImpl implements IWxService {
    private static final Logger loggerPay = LoggerFactory.getLogger(WxServiceImpl.class);
    @Autowired
    private WxPayFactory wxPayFactory;
    private final ReentrantLock transferNotifyLock = new ReentrantLock();

    @Override
    public String getUserOpenId(String jsCode) {
        wxPayFactory.instance();
        String appKey = wxPayFactory.getProperties().getAppId();
        String appSecret = wxPayFactory.getProperties().getAppSecret();
        String url = wxPayFactory.getProperties().getAccessToken();
        String param = new StringBuffer("grant_type=authorization_code").
                append("&appid=").append(appKey).append("&secret=").
                append(appSecret).append("&js_code=").append(jsCode).toString();
        return HttpUtils.sendGet(url, param);
    }

    @Override
    public TransferBillsResult payPrize(Long userId, BigDecimal money) throws WxPayException {
        WxPayService wxPayService = wxPayFactory.instance();
        // 创建请求参数
        TransferBillsRequest request = new TransferBillsRequest();
        // 设置商户关联的appid，微信公众号、小程序id
        request.setAppid(wxPayFactory.getProperties().getAppId());
        // 设置商户订单号，需保持唯一性,由数字字母组成
        request.setOutBillNo("Prize" + System.currentTimeMillis());
        // 设置转账场景ID
        request.setTransferSceneId("1000");
        // 设置收款方真实姓名，转账金额 >= 2,000元时，该笔明细必须填写
        //request.setUserName("赖xx");
        // 设置Openid，必须为关联appid的小程序或者公众号用户的openids
        request.setOpenid("oQowC7iaMJBIO2MM_fB7ffFbUHyQ");
        // 转账备注，用户收款时可见该备注信息，UTF8编码，最多允许32个字符
        request.setTransferRemark("恭喜获得现金奖励!");
        // 转账金额，单位为分
        request.setTransferAmount(money.multiply(new BigDecimal(100)).intValue());
        // 转账成功回调接口
        //request.setNotifyUrl();
        // 用户收款时感知到的收款原因将根据转账场景自动展示默认内容
        List<TransferBillsRequest.TransferSceneReportInfo> transferSceneReportInfos = new ArrayList<>();
        //创建转账场景信息对象
        TransferBillsRequest.TransferSceneReportInfo.TransferSceneReportInfoBuilder newBuilder = TransferBillsRequest.TransferSceneReportInfo.newBuilder();
        TransferBillsRequest.TransferSceneReportInfo info = newBuilder.build();
        //设置转账场景信息类型,这个要特别注意，要和商户设置的场景ID对应
        info.setInfoType("活动名称");
        //设置转账场景信息内容
        info.setInfoContent("爱阅读，赢现金");
        transferSceneReportInfos.add(info);
        info = newBuilder.build();
        //设置转账场景信息类型,这个要特别注意，要和商户设置的场景ID对应
        info.setInfoType("奖励说明");
        //设置转账场景信息内容
        info.setInfoContent("恭喜您获得现金奖励");
        transferSceneReportInfos.add(info);
        request.setTransferSceneReportInfos(transferSceneReportInfos);
        // 调用转账接口
        TransferBillsResult result = wxPayService.getTransferService().transferBills(request);
        return result;
    }

    @Override
    public TransferBillsCancelResult cancelTransfer(@PathVariable("outBillNo") String outBillNo) throws WxPayException {
        WxPayService wxPayService = wxPayFactory.instance();
        // 调用转账接口
        TransferBillsCancelResult result = wxPayService.getTransferService().transformBillsCancel(outBillNo);
        return result;
    }

    @Override
    public TransferBillsGetResult getTransferOrder(String outBillNo) throws WxPayException {
        WxPayService wxPayService = wxPayFactory.instance();
        TransferBillsGetResult result = wxPayService.getTransferService().getBillsByOutBillNo(outBillNo);
        return result;

    }
    @Override
    public String payTransferNotify(SignatureHeader header,String notifyData) {
        if (transferNotifyLock.tryLock()) {// 获取锁
            try {
                WxPayService wxPayService = wxPayFactory.instance();
                TransferBillsNotifyResult transferBillsNotifyResult = wxPayService.getTransferService().parseTransferBillsNotifyResult(notifyData, header);

                loggerPay.info("TransferBillsNotifyResult:" + JSON.toJSONString(transferBillsNotifyResult));

                // 获取基本信息
                String state = transferBillsNotifyResult.getResult().getState();
                if (WxPayConstants.TransformBillState.SUCCESS.equals(state)) {
                    loggerPay.info("转账成功");
                    return WxPayNotifyV3Response.success("转账成功");
                } else if (WxPayConstants.TransformBillState.CANCELING.equals(state)) {
                    loggerPay.info("转账已撤销");
                    return WxPayNotifyV3Response.success("转账已撤销");
                } else if (WxPayConstants.TransformBillState.FAIL.equals(state)) {
                    loggerPay.info("转账失败");
                    return WxPayNotifyV3Response.success("转账失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                loggerPay.info("回调通知payNotify失败 " + e.getMessage());
                return WxPayNotifyV3Response.fail("失败");
            } finally {
                transferNotifyLock.unlock();// 释放锁
            }
        } else {
            loggerPay.info("锁获取失败");
            return WxPayNotifyV3Response.fail("锁获取失败");
        }

        loggerPay.info("回调通知payNotify结束：失败");
        return WxPayNotifyV3Response.fail("失败");
    }

    /**
     * 读取请求流数据
     *
     * @param request 请求对象
     * @return 请求流数据字符串
     */
    public String fetchRequest2Str(HttpServletRequest request) {
        String reqStr = null;
        BufferedReader streamReader = null;
        try {
            streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            reqStr = responseStrBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (streamReader != null) {
                    streamReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reqStr;
    }

}
