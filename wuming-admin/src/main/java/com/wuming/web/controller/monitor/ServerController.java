package com.wuming.web.controller.monitor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.framework.web.domain.Server;

/**
 * 服务器监控
 * 
 * @author wuming
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController
{
    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public AjaxResult getInfo() throws Exception
    {
//        Server server = new Server();
//        server.copyTo();
        return AjaxResult.success();
    }
}
