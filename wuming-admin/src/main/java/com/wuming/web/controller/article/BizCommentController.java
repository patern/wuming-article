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
import com.wuming.article.domain.BizComment;
import com.wuming.article.service.IBizCommentService;
import com.wuming.common.utils.poi.ExcelUtil;
import com.wuming.common.core.page.TableDataInfo;

/**
 * 打卡评论Controller
 * 
 * @author patern
 * @date 2025-05-27
 */
@RestController
@RequestMapping("/article/comment")
public class BizCommentController extends BaseController
{
    @Autowired
    private IBizCommentService bizCommentService;

    /**
     * 查询打卡评论列表
     */
    @PreAuthorize("@ss.hasPermi('article:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizComment bizComment)
    {
        startPage();
        List<BizComment> list = bizCommentService.selectBizCommentList(bizComment);
        return getDataTable(list);
    }

    /**
     * 导出打卡评论列表
     */
    @PreAuthorize("@ss.hasPermi('article:comment:export')")
    @Log(title = "打卡评论", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizComment bizComment)
    {
        List<BizComment> list = bizCommentService.selectBizCommentList(bizComment);
        ExcelUtil<BizComment> util = new ExcelUtil<BizComment>(BizComment.class);
        util.exportExcel(response, list, "打卡评论数据");
    }

    /**
     * 获取打卡评论详细信息
     */
    @PreAuthorize("@ss.hasPermi('article:comment:query')")
    @GetMapping(value = "/{commentId}")
    public AjaxResult getInfo(@PathVariable("commentId") Long commentId)
    {
        return success(bizCommentService.selectBizCommentByCommentId(commentId));
    }

    /**
     * 新增打卡评论
     */
    @PreAuthorize("@ss.hasPermi('article:comment:add')")
    @Log(title = "打卡评论", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizComment bizComment)
    {
        return toAjax(bizCommentService.insertBizComment(bizComment));
    }

    /**
     * 修改打卡评论
     */
    @PreAuthorize("@ss.hasPermi('article:comment:edit')")
    @Log(title = "打卡评论", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizComment bizComment)
    {
        return toAjax(bizCommentService.updateBizComment(bizComment));
    }

    /**
     * 删除打卡评论
     */
    @PreAuthorize("@ss.hasPermi('article:comment:remove')")
    @Log(title = "打卡评论", businessType = BusinessType.DELETE)
	@DeleteMapping("/{commentIds}")
    public AjaxResult remove(@PathVariable Long[] commentIds)
    {
        return toAjax(bizCommentService.deleteBizCommentByCommentIds(commentIds));
    }
}
