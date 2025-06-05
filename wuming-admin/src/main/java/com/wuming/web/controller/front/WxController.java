package com.wuming.web.controller.front;

import com.alibaba.fastjson2.JSON;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Response;
import com.github.binarywang.wxpay.bean.transfer.*;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.wuming.article.pay.WxPayFactory;
import com.wuming.article.service.IWxService;
import com.wuming.common.annotation.Anonymous;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.utils.SecurityUtils;
import com.wuming.common.utils.http.HttpUtils;
import com.wuming.web.controller.front.vo.PayVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/order/wx")
public class WxController  extends BaseController{
    private static final Logger loggerPay = LoggerFactory.getLogger(WxController.class);
    private final ReentrantLock transferNotifyLock = new ReentrantLock();
    @Autowired
    private IWxService wxService;

    @RequestMapping("/getOpenId/{jsCode}")
    @Anonymous
    public AjaxResult getOpenId(@PathVariable("jsCode") String jsCode) {
        try {
            return success(wxService.getUserOpenId(jsCode));
        } catch (Exception e) {
            loggerPay.error("获取openId失败",e);
        }
        return  error("获取openId失败!");
    }

    @RequestMapping("/entPay")
    public AjaxResult entPay(@RequestBody PayVo pay) {
        if (null==pay || null==pay.getUserId() || null==pay.getMoney()){
            return error("参数不完整，无法提现");
        }
        if ( null==pay.getMoney()|| pay.getMoney().compareTo(new BigDecimal(0))<=0){
            return error("提现金额非法");
        }
        if (SecurityUtils.getLoginUser().getUser().getUserId().equals(pay.getUserId())){
            return error("非用户本人,不能提现");
        }
        try {
            wxService.payPrize(pay.getUserId(),pay.getMoney());
        } catch (WxPayException e) {
            loggerPay.error("提现失败",e);
        }
        return error("申请提现失败");
    }

    @RequestMapping("/cancelTransfer/{outBillNo}")
    public AjaxResult cancelTransfer(@PathVariable("outBillNo") String outBillNo) {
        try {
           return  success(wxService.cancelTransfer(outBillNo));
        } catch (WxPayException e) {
            loggerPay.error("撤销转账失败",e);
        }
        return error("撤销转账失败");
    }

    /**
     * 查询转账订单
     *
     * @param outBillNo 商户订单号
     * @return 查询转账订单结果
     */
    @RequestMapping("/getTransferOrder/{outBillNo}")
    //"查询转账订单")
    public AjaxResult getTransferOrder(@PathVariable("outBillNo") String outBillNo) {
        try {
            return success(wxService.getTransferOrder(outBillNo));
        } catch (WxPayException e) {
            loggerPay.error("查询转账失败",e);
        }
        return error("查询转账失败");
    }


    /**
     * 商家转账回调通知
     *
     * @return 转账回调通知结果字符串
     */
    @RequestMapping("/payTransferNotify")
    public String payTransferNotify(HttpServletRequest request) {
        loggerPay.info("回调通知payNotify开始");

        SignatureHeader header = new SignatureHeader();
        header.setTimeStamp(request.getHeader("Wechatpay-Timestamp"));
        header.setNonce(request.getHeader("Wechatpay-Nonce"));
        header.setSignature(request.getHeader("Wechatpay-Signature"));
        header.setSerial(request.getHeader("Wechatpay-Serial"));

        String notifyData = fetchRequest2Str(request);
        loggerPay.info("SignatureHeader:" + JSON.toJSONString(header));
        loggerPay.info("notifyData:" + notifyData);
        return wxService.payTransferNotify(header,notifyData);
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