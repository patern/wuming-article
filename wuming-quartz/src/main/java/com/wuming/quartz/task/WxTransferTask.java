package com.wuming.quartz.task;

import com.github.binarywang.wxpay.bean.transfer.TransferBillsGetResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.wuming.article.domain.BizPrize;
import com.wuming.article.pay.WxPayFactory;
import com.wuming.article.service.IBizPrizeService;
import com.wuming.common.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务调度测试
 *
 * @author wuming
 */
@Component("wxTask")
public class WxTransferTask {
    private static final Logger logger = LoggerFactory.getLogger(WxTransferTask.class);
    @Autowired
    private IBizPrizeService bizPrizeService;
    @Autowired
    private WxPayFactory wxPayFactory;

    public void syncOutNoState(String outNo){
        WxPayService wxPayService = wxPayFactory.instance();
        BizPrize query = new BizPrize();
        if (StringUtils.isNotBlank(outNo)){
            query.setPrizeNo(outNo);
        }
        List<BizPrize> bizPrizeList = bizPrizeService.selectUnCompleteBill(query);
        if(CollectionUtils.isNotEmpty(bizPrizeList)){
            logger.info("查询到{}条未完成提现，",bizPrizeList.size());
            for (BizPrize prize: bizPrizeList){
                try {
                    TransferBillsGetResult result = wxPayService.getTransferService().getBillsByOutBillNo(prize.getPrizeNo());
                    BizPrize updateBizPrize = new BizPrize();
                    updateBizPrize.setPrizeId(prize.getPrizeId());
                    updateBizPrize.setStatus(result.getState());
                    updateBizPrize.setUpdateBy("admin");
                    bizPrizeService.updateBizPrize(updateBizPrize);
                } catch (WxPayException e) {
                    logger.info("查询转账{}错误，",prize.getPrizeNo(),e);
                }
            }
        }
        logger.info("处理未完成提现完成");
    }

    public void syncPayState() {
        syncOutNoState(null);
    }
}
