package com.wuming.web.controller.article;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.wuming.article.domain.BizArticle;
import com.wuming.article.domain.BizUser;
import com.wuming.article.dto.BizUserQuery;
import com.wuming.article.service.IBizUserService;
import com.wuming.web.controller.front.vo.BizArticleVo;
import com.wuming.web.controller.front.vo.BizPrizeVo;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
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
import com.wuming.article.domain.BizPrize;
import com.wuming.article.service.IBizPrizeService;
import com.wuming.common.utils.poi.ExcelUtil;
import com.wuming.common.core.page.TableDataInfo;

/**
 * 用户提现Controller
 * 
 * @author wuming
 * @date 2025-06-05
 */
@RestController
@RequestMapping("/article/prize")
public class BizPrizeController extends BaseController
{
    @Autowired
    private IBizPrizeService bizPrizeService;
    @Autowired
    private IBizUserService bizUserService;

    /**
     * 查询用户提现列表
     */
    @PreAuthorize("@ss.hasPermi('article:prize:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizPrize bizPrize)
    {
        startPage();
        List<BizPrize> list = bizPrizeService.selectBizPrizeList(bizPrize);

        Set<Long> userIds = list.stream().map(BizPrize::getUserId).collect(Collectors.toSet());
        BizUserQuery query1 = new BizUserQuery();
        query1.setUserIds(userIds);
//        query1.setStatus("0");
        List<BizUser> users = bizUserService.selectBizUser(query1);
        Map<Long, BizUser> userMap = users.stream().collect(Collectors.toMap(BizUser::getUserId, v -> v));

        List<BizPrizeVo> subComments = Lists.newArrayList();

        for (BizPrize article : list) {
            BizPrizeVo vo = new BizPrizeVo();
            BeanUtils.copyProperties(article, vo);
            BizUser u = userMap.get(article.getUserId());
            if (null != u) {
                vo.setUserName(u.getNickName());
                vo.setSchoolName(u.getSchoolName());
            }
            subComments.add(vo);
        }
        return getDataTable(subComments);
    }

    /**
     * 导出用户提现列表
     */
    @PreAuthorize("@ss.hasPermi('article:prize:export')")
    @Log(title = "用户提现", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizPrize bizPrize)
    {
        List<BizPrize> list = bizPrizeService.selectBizPrizeList(bizPrize);
        ExcelUtil<BizPrize> util = new ExcelUtil<BizPrize>(BizPrize.class);
        util.exportExcel(response, list, "用户提现数据");
    }

    /**
     * 获取用户提现详细信息
     */
    @PreAuthorize("@ss.hasPermi('article:prize:query')")
    @GetMapping(value = "/{prizeId}")
    public AjaxResult getInfo(@PathVariable("prizeId") Long prizeId)
    {
        return success(bizPrizeService.selectBizPrizeByPrizeId(prizeId));
    }

    /**
     * 新增用户提现
     */
    @PreAuthorize("@ss.hasPermi('article:prize:add')")
    @Log(title = "用户提现", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizPrize bizPrize)
    {
        return toAjax(bizPrizeService.insertBizPrize(bizPrize));
    }

    /**
     * 修改用户提现
     */
    @PreAuthorize("@ss.hasPermi('article:prize:edit')")
    @Log(title = "用户提现", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizPrize bizPrize)
    {
        return toAjax(bizPrizeService.updateBizPrize(bizPrize));
    }

    /**
     * 删除用户提现
     */
    @PreAuthorize("@ss.hasPermi('article:prize:remove')")
    @Log(title = "用户提现", businessType = BusinessType.DELETE)
	@DeleteMapping("/{prizeIds}")
    public AjaxResult remove(@PathVariable Long[] prizeIds)
    {
        return toAjax(bizPrizeService.deleteBizPrizeByPrizeIds(prizeIds));
    }
}
