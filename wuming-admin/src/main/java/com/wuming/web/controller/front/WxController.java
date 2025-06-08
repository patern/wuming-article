package com.wuming.web.controller.front;

import com.alibaba.fastjson2.JSON;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.wuming.article.domain.BizArticle;
import com.wuming.article.domain.BizPrize;
import com.wuming.article.dto.BizArticleCountDto;
import com.wuming.article.service.IBizArticleService;
import com.wuming.article.service.IBizPrizeService;
import com.wuming.article.service.IWxService;
import com.wuming.common.annotation.Anonymous;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.utils.SecurityUtils;
import com.wuming.web.controller.front.vo.BizArticleVo;
import com.wuming.web.controller.front.vo.PayVo;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.List;

@RestController
@RequestMapping("/order/wx")
public class WxController extends BaseController {
    private static final Logger loggerPay = LoggerFactory.getLogger(WxController.class);
    @Autowired
    private IWxService wxService;
    @Autowired
    private IBizPrizeService prizeService;
    @Autowired
    private IBizArticleService bizArticleService;

    @RequestMapping("/getOpenId/{jsCode}")
    @Anonymous
    public AjaxResult getOpenId(@PathVariable("jsCode") String jsCode) {
        try {
            return success(wxService.getUserOpenId(jsCode));
        } catch (Exception e) {
            loggerPay.error("获取openId失败", e);
        }
        return error("获取openId失败!");
    }

    @RequestMapping("/entPay")
    public AjaxResult entPay(@RequestBody PayVo pay) {
        if (null == pay || null == pay.getUserId() || null == pay.getMoney()) {
            return error("参数不完整，无法提现");
        }
        if (null == pay.getMoney() || pay.getMoney().compareTo(new BigDecimal(0.1)) <= 0) {
            return error("提现金额非法,不能少于0.1");
        }
        if (!SecurityUtils.getLoginUser().getUser().getUserId().equals(pay.getUserId())) {
            return error("非用户本人,不能提现");
        }

        BizArticle article = new BizArticleVo();
        article.setUserId(pay.getUserId());
        List<BizArticleCountDto> countDtos = bizArticleService.selectBizArticleSumList(article);
        //已经转成功或者锁定中的金额
        BizPrize prize = new BizPrize();
        prize.setUserId(pay.getUserId());
//        prize.setStatus(WxPayConstants.TransformBillState.SUCCESS);
        List<BizPrize> prizes = prizeService.selectBizPrizeList(prize);
        BigDecimal total = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(countDtos)) {
            total = countDtos.get(0).getTotalPrize();
            if (CollectionUtils.isNotEmpty(prizes)) {
                BigDecimal sum = prizes.stream().map(e -> e.getMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);
                total = total.subtract(sum);
            }
        }
        if (total.compareTo(pay.getMoney())<0) {
            return error("余额不足，不能提现");
        }
        try {
            return success(wxService.payPrize(pay.getUserId(), pay.getMoney()));
        } catch (WxPayException e) {
            loggerPay.error("提现失败", e);
        }
        return error("申请提现失败");
    }

    @RequestMapping("/cancelTransfer/{outBillNo}")
    public AjaxResult cancelTransfer(@PathVariable("outBillNo") String outBillNo) {
        try {
            return success(wxService.cancelTransfer(outBillNo));
        } catch (WxPayException e) {
            loggerPay.error("撤销转账失败", e);
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
            loggerPay.error("查询转账失败", e);
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
        return wxService.payTransferNotify(header, notifyData);
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