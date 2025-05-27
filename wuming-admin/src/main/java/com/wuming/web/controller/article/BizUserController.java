package com.wuming.web.controller.article;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wuming.common.annotation.Log;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.enums.BusinessType;
import com.wuming.article.domain.BizUser;
import com.wuming.article.service.IBizUserService;
import com.wuming.common.utils.poi.ExcelUtil;
import com.wuming.common.core.page.TableDataInfo;

/**
 * 用户Controller
 * 
 * @author patern
 * @date 2025-05-27
 */
@RestController
@RequestMapping("/article/user")
public class BizUserController extends BaseController
{
    @Autowired
    private IBizUserService bizUserService;

    /**
     * 查询用户列表
     */
    @PreAuthorize("@ss.hasPermi('article:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizUser bizUser)
    {
        startPage();
        List<BizUser> list = bizUserService.selectBizUserList(bizUser);
        return getDataTable(list);
    }

    /**
     * 导出用户列表
     */
    @PreAuthorize("@ss.hasPermi('article:user:export')")
    @Log(title = "用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizUser bizUser)
    {
        List<BizUser> list = bizUserService.selectBizUserList(bizUser);
        ExcelUtil<BizUser> util = new ExcelUtil<BizUser>(BizUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 获取用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('article:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(bizUserService.selectBizUserByUserId(userId));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('article:user:add')")
    @Log(title = "用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizUser bizUser)
    {
        return toAjax(bizUserService.insertBizUser(bizUser));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('article:user:edit')")
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizUser bizUser)
    {
        return toAjax(bizUserService.updateBizUser(bizUser));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('article:user:remove')")
    @Log(title = "用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(bizUserService.deleteBizUserByUserIds(userIds));
    }
}
