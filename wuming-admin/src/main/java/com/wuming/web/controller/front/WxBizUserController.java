package com.wuming.web.controller.front;

import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.wuming.article.domain.BizArticle;
import com.wuming.article.domain.BizPrize;
import com.wuming.article.domain.BizUser;
import com.wuming.article.dto.*;
import com.wuming.article.service.IBizArticleService;
import com.wuming.article.service.IBizPrizeService;
import com.wuming.article.service.IBizUserService;
import com.wuming.article.service.IWxService;
import com.wuming.common.annotation.Anonymous;
import com.wuming.common.annotation.Log;
import com.wuming.common.constant.Constants;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.core.domain.entity.SysUser;
import com.wuming.common.core.domain.model.LoginUser;
import com.wuming.common.core.page.TableDataInfo;
import com.wuming.common.enums.BusinessType;
import com.wuming.common.utils.DateUtils;
import com.wuming.common.utils.MessageUtils;
import com.wuming.common.utils.SecurityUtils;
import com.wuming.common.utils.StringUtils;
import com.wuming.common.utils.ip.IpUtils;
import com.wuming.framework.manager.AsyncManager;
import com.wuming.framework.manager.factory.AsyncFactory;
import com.wuming.framework.web.service.TokenService;
import com.wuming.system.service.ISysUserService;
import com.wuming.web.controller.front.vo.BizArticleVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户Controller
 *
 * @author patern
 * @date 2025-05-27
 */
@RestController
@RequestMapping("/front/wx/user")
public class WxBizUserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(WxBizUserController.class);
    @Autowired
    private IBizUserService bizUserService;
    @Autowired
    private IBizArticleService bizArticleService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private IBizPrizeService prizeService;
    @Autowired
    private IWxService wxService;
    @GetMapping("/article/list")
    public TableDataInfo list(BizArticleQuery bizArticle) {
        startPage();
        bizArticle.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        List<BizArticle> list = bizArticleService.selectBizArticleList(bizArticle);
        return getDataTable(list);
    }
    @GetMapping("/prize/list")
    public TableDataInfo list(BizPrize bizPrize) {
        startPage();
        bizPrize.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        bizPrize.setStatus(WxPayConstants.TransformBillState.SUCCESS);
        List<BizPrize> list = prizeService.selectBizPrizeList(bizPrize);
        return getDataTable(list);
    }
    /**
     * 根据微信openId获取用户信息
     */
    @GetMapping(value = "/{openId}")
    @Anonymous
    public AjaxResult getInfo(@PathVariable("openId") String jsCode) {
        if (StringUtils.isEmpty(jsCode)) {
            return error("用户jsCode不能为空");
        }
        String openId = null;
        try {
            openId = wxService.getUserOpenId(jsCode);
        } catch (Exception e) {
            logger.error("获取openId失败",e);
        }
        if (StringUtils.isEmpty(openId)) {
            return error("获取用户信息失败");
        }
        BizUser user = new BizUser();
        user.setOpenId(openId);
        List<BizUser> users = bizUserService.selectBizUserList(user);
        if (CollectionUtils.isEmpty(users)) {
            return success();
        } else {
            BizUser bizUser = users.get(0);
            SysUser sysUser = new SysUser();
            sysUser.setUserId(bizUser.getUserId());
            sysUser.setUserName(bizUser.getUserName());
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(openId, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
            LoginUser loginUser = new LoginUser(user.getUserId(), 100L, sysUser, Sets.newHashSet());
            recordLoginInfo(user.getUserId());
            // 生成token
            String token =  tokenService.createToken(loginUser);
            bizUser.setToken(token);
            BizArticle article = new BizArticleVo();
            article.setUserId(bizUser.getUserId());
            List<BizArticleCountDto> countDtos = bizArticleService.selectBizArticleSumList(article);

            BizPrize prize = new BizPrize();
            prize.setUserId(bizUser.getUserId());
//            prize.setStatus(WxPayConstants.TransformBillState.SUCCESS);
            List<BizPrize> prizes = prizeService.selectBizPrizeList(prize);
            if (CollectionUtils.isNotEmpty(countDtos)){
                bizUser.setPrizeTotal(countDtos.get(0).getTotalPrize());
                if (CollectionUtils.isNotEmpty(prizes)){
                    BigDecimal sum = prizes.stream().map(e->e.getMoney()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    if (null!=bizUser.getPrizeTotal()){
                        BigDecimal left = bizUser.getPrizeTotal().subtract(sum);
                        bizUser.setPrizeTotal(left);
                    }
                }
            }
            return success(bizUser);
        }
    }
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }

    /**
     * 新增用户/注册
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody BizUser bizUser) {
        if (StringUtils.isEmpty(bizUser.getOpenId())) {
            return error("用户openId不能为空");
        }
        String openId = null;
        try {
            openId = wxService.getUserOpenId(bizUser.getOpenId());
        } catch (Exception e) {
            logger.error("获取openId失败",e);
        }
        BizUser user = new BizUser();
        user.setOpenId(openId);
        List<BizUser> users = bizUserService.selectBizUserList(user);
        if (CollectionUtils.isNotEmpty(users)) {
            return error("用户已经注册");
        }
        bizUser.setOpenId(openId);
        return toAjax(bizUserService.insertBizUser(bizUser));
    }

    /**
     * 修改用户
     */
    @Log(title = "打卡用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizUser bizUser) {
        return toAjax(bizUserService.updateBizUser(bizUser));
    }

}
