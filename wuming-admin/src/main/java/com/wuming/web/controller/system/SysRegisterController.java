package com.wuming.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.core.domain.model.RegisterBody;
import com.wuming.common.utils.StringUtils;
import com.wuming.framework.web.service.SysRegisterService;
import com.wuming.system.service.ISysConfigService;

/**
 * 注册验证
 * 
 * @author wuming
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
