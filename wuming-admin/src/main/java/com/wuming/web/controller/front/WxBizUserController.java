package com.wuming.web.controller.front;

import com.wuming.article.domain.BizUser;
import com.wuming.article.service.IBizUserService;
import com.wuming.common.annotation.Anonymous;
import com.wuming.common.annotation.Log;
import com.wuming.common.constant.Constants;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.core.domain.entity.SysUser;
import com.wuming.common.core.domain.model.LoginUser;
import com.wuming.common.enums.BusinessType;
import com.wuming.common.utils.DateUtils;
import com.wuming.common.utils.MessageUtils;
import com.wuming.common.utils.StringUtils;
import com.wuming.common.utils.ip.IpUtils;
import com.wuming.framework.manager.AsyncManager;
import com.wuming.framework.manager.factory.AsyncFactory;
import com.wuming.framework.web.service.TokenService;
import com.wuming.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller
 *
 * @author patern
 * @date 2025-05-27
 */
@RestController
@RequestMapping("/front/wx/user")
public class WxBizUserController extends BaseController {
    @Autowired
    private IBizUserService bizUserService;

    @Autowired
    private ISysUserService userService;
    @Autowired
    private TokenService tokenService;


    /**
     * 根据微信openId获取用户信息
     */
    @GetMapping(value = "/{openId}")
    @Anonymous
    public AjaxResult getInfo(@PathVariable("openId") String openId) {
        if (StringUtils.isEmpty(openId)) {
            return error("用户openId不能为空");
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
        BizUser user = new BizUser();
        user.setOpenId(bizUser.getOpenId());
        List<BizUser> users = bizUserService.selectBizUserList(user);
        if (CollectionUtils.isNotEmpty(users)) {
            return error("用户已经注册");
        }
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
