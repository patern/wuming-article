package com.wuming.web.controller.front;

import com.wuming.article.domain.BizUser;
import com.wuming.article.service.IBizUserService;
import com.wuming.common.annotation.Log;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.enums.BusinessType;
import com.wuming.common.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
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


    /**
     * 根据微信openId获取用户信息
     */
    @GetMapping(value = "/{openId}")
    public AjaxResult getInfo(@PathVariable("openId") String openId) {
        BizUser user = new BizUser();
        user.setOpenId(openId);
        List<BizUser> users = bizUserService.selectBizUserList(user);
        if (CollectionUtils.isEmpty(users)) {
            return success();
        } else {
            return success(users.get(0));
        }
    }

    /**
     * 新增用户
     */
    @Log(title = "打卡用户", businessType = BusinessType.INSERT)
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
