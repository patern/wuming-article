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
import com.wuming.article.domain.BizArticle;
import com.wuming.article.service.IBizArticleService;
import com.wuming.common.utils.poi.ExcelUtil;
import com.wuming.common.core.page.TableDataInfo;

/**
 * 文章Controller
 * 
 * @author patern
 * @date 2025-05-27
 */
@RestController
@RequestMapping("/article/article")
public class BizArticleController extends BaseController
{
    @Autowired
    private IBizArticleService bizArticleService;

    /**
     * 查询文章列表
     */
    @PreAuthorize("@ss.hasPermi('article:article:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizArticle bizArticle)
    {
        startPage();
        List<BizArticle> list = bizArticleService.selectBizArticleList(bizArticle);
        return getDataTable(list);
    }

    /**
     * 导出文章列表
     */
    @PreAuthorize("@ss.hasPermi('article:article:export')")
    @Log(title = "文章", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizArticle bizArticle)
    {
        List<BizArticle> list = bizArticleService.selectBizArticleList(bizArticle);
        ExcelUtil<BizArticle> util = new ExcelUtil<BizArticle>(BizArticle.class);
        util.exportExcel(response, list, "文章数据");
    }

    /**
     * 获取文章详细信息
     */
    @PreAuthorize("@ss.hasPermi('article:article:query')")
    @GetMapping(value = "/{articleId}")
    public AjaxResult getInfo(@PathVariable("articleId") Long articleId)
    {
        return success(bizArticleService.selectBizArticleByArticleId(articleId));
    }

    /**
     * 新增文章
     */
    @PreAuthorize("@ss.hasPermi('article:article:add')")
    @Log(title = "文章", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizArticle bizArticle)
    {
        return toAjax(bizArticleService.insertBizArticle(bizArticle));
    }

    /**
     * 修改文章
     */
    @PreAuthorize("@ss.hasPermi('article:article:edit')")
    @Log(title = "文章", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizArticle bizArticle)
    {
        return toAjax(bizArticleService.updateBizArticle(bizArticle));
    }

    /**
     * 删除文章
     */
    @PreAuthorize("@ss.hasPermi('article:article:remove')")
    @Log(title = "文章", businessType = BusinessType.DELETE)
	@DeleteMapping("/{articleIds}")
    public AjaxResult remove(@PathVariable Long[] articleIds)
    {
        return toAjax(bizArticleService.deleteBizArticleByArticleIds(articleIds));
    }
}
