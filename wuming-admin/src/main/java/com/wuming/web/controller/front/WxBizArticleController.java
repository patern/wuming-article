package com.wuming.web.controller.front;

import com.wuming.article.domain.BizArticle;
import com.wuming.article.domain.BizComment;
import com.wuming.article.domain.BizUser;
import com.wuming.article.dto.BizCommentCountDto;
import com.wuming.article.dto.BizCommentQuery;
import com.wuming.article.dto.BizUserQuery;
import com.wuming.article.service.IBizArticleService;
import com.wuming.article.service.IBizCommentService;
import com.wuming.article.service.IBizUserService;
import com.wuming.common.annotation.Anonymous;
import com.wuming.common.annotation.Log;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.core.domain.model.LoginUser;
import com.wuming.common.core.page.TableDataInfo;
import com.wuming.common.enums.BusinessType;
import com.wuming.common.oss.core.OssClient;
import com.wuming.common.oss.entity.UploadResult;
import com.wuming.common.oss.factory.OssFactory;
import com.wuming.common.utils.SecurityUtils;
import com.wuming.common.utils.poi.ExcelUtil;
import com.wuming.web.controller.front.vo.BizArticleVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 打卡Controller
 *
 * @author patern
 * @date 2025-05-27
 */
@RestController
@RequestMapping("/front/wx/article")
public class WxBizArticleController extends BaseController {
    @Autowired
    private IBizArticleService bizArticleService;
    @Autowired
    private IBizCommentService bizCommentService;
    @Autowired
    private IBizUserService bizUserService;
    @Autowired
    private OssFactory ossFactory;


    /**
     * 查询打卡列表
     * 查询用户自己的设置 userId，并且状态可以是空，查询所有
     * 查询详情的 无须设置userId, 但是需要设置状态是 0，表示查询状态正常的，没有被用户自己作废，或者被管理员下架的
     * 小程序点击评论和点赞的时候必须获取当前用户信息，如果判断获取的用户id和评论和点赞的userId有当前用户，就表示为点亮状态，否则为未点亮装
     * 并且显示评论总条数（只显示前5条，更多的需要点击详情查看，进入详情就需要登陆），点赞数量
     */
    @GetMapping("/list")
    @Anonymous
    public TableDataInfo list(BizArticle bizArticle) {
        startPage();
        List<BizArticle> list = bizArticleService.selectBizArticleList(bizArticle);
        if (CollectionUtils.isEmpty(list)) {
            return getDataTable(list);
        }
        List<Long> ids = list.stream().map(BizArticle::getArticleId).collect(Collectors.toList());
        BizCommentQuery query = new BizCommentQuery();
        query.setArticleIds(ids);
        query.setStatus("0");
        List<BizCommentCountDto> comments = bizCommentService.selectBizCommentCount(query);

        List<Long> userIds = list.stream().map(BizArticle::getUserId).collect(Collectors.toList());
        BizUserQuery query1 = new BizUserQuery();
        query1.setUserIds(userIds);
        query1.setStatus("0");
        List<BizUser> users = bizUserService.selectBizUser(query1);
        Map<Long, BizUser> userMap = users.stream().collect(Collectors.toMap(BizUser::getUserId, v -> v));

        List<BizArticleVo> subComments = Lists.newArrayList();

        Map<Long, List<BizCommentCountDto>> commentMap = comments.stream().collect(Collectors.groupingBy(BizCommentCountDto::getArticleId));

        for (BizArticle article : list) {
            BizArticleVo vo = new BizArticleVo();
            BeanUtils.copyProperties(article, vo);
            BizUser u = userMap.get(article.getUserId());
            if (null != u) {
                vo.setUserName(u.getNickName());
                vo.setSchoolName(u.getSchoolName());
            }
            List<BizCommentCountDto> comments1 = commentMap.get(article.getArticleId());
            if (CollectionUtils.isNotEmpty(comments1)) {
                List<BizCommentCountDto> comments11 = comments1.stream().filter(e -> e.getCommentType().equals("1")).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(comments11)) {
                    vo.setUpvoteCount(comments11.get(0).getCount());
                }
                List<BizCommentCountDto> comments12 = comments1.stream().filter(e -> e.getCommentType().equals("2")).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(comments12)) {
                    vo.setCommentCounts(comments12.get(0).getCount());
                }
            } else {
                vo.setUpvoteCount(0l);
                vo.setCommentCounts(0l);
            }
            subComments.add(vo);
        }
        return getDataTable(subComments);
    }

    /**
     * 获取打卡详情
     *
     * @param articleId
     * @return
     */
    @GetMapping("/get/{articleId}")
    public AjaxResult get(@PathVariable("articleId") Long articleId) {
        startPage();
        BizArticle article = bizArticleService.selectBizArticleByArticleId(articleId);
        if (null == article) {
            return success(null);
        }
        BizComment query = new BizComment();
        query.setArticleId(articleId);
        query.setStatus("0");
        List<BizComment> comments = bizCommentService.selectBizCommentList(query);
        BizArticleVo vo = new BizArticleVo();
        BeanUtils.copyProperties(article, vo);
        if (CollectionUtils.isEmpty(comments)) {
            return success(vo);
        }
        List<Long> userIds = Lists.newArrayList();
        userIds.add(article.getUserId());
        BizUserQuery query1 = new BizUserQuery();
        query1.setUserIds(userIds);
        query1.setStatus("0");
        List<BizUser> users = bizUserService.selectBizUser(query1);
        Map<Long, BizUser> userMap = users.stream().collect(Collectors.toMap(BizUser::getUserId, v -> v));

        if (CollectionUtils.isNotEmpty(comments)) {
            vo.setUpVotes(comments.stream().filter(e -> e.getCommentType().equals("1")).collect(Collectors.toList()));
            vo.setComments(comments.stream().filter(e -> e.getCommentType().equals("2")).collect(Collectors.toList()));
            vo.setUpvoteCount(Long.valueOf(vo.getUpVotes().size()));
            vo.setCommentCounts(Long.valueOf(vo.getComments().size()));
            //是否有点赞当前打卡
            vo.setUpvote(comments.stream().filter(e -> e.getCommentType().equals("1")
                    && e.getUserId().equals(SecurityUtils.getUserId())).collect(Collectors.toList()).size()>0);
            BizUser u = userMap.get(article.getUserId());
            if (null != u) {
                vo.setUserName(u.getNickName());
                vo.setSchoolName(u.getSchoolName());
            }
        }
        return success(vo);
    }

    /**
     * 点赞或者评论
     * userId，articleId，status 0:正常，1取消点赞，或者删除评论， commentType：评论类型（1点赞 2评论）
     * 如果是删除评论或者点赞，则需要传递评论的id commentId，删除自己删除自己的评论，或者取消自己的点赞
     */
    @PostMapping(value = "/comment")
    @Log(title = "评论/点赞", businessType = BusinessType.INSERT)
    public AjaxResult getInfo(@RequestBody BizComment comment) {
        //如果是正常，则新增
        comment.setStatus("0");
        return toAjax(bizCommentService.insertBizComment(comment));
    }

    /**
     * 取消点赞或者评论
     */
    @DeleteMapping(value = "/comment")
    @Log(title = "评论/点赞", businessType = BusinessType.DELETE)
    public AjaxResult cancelUpVote(@RequestBody BizComment comment) {
        if (null == comment.getCommentId()) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            return toAjax(bizCommentService.deleteByUserId(loginUser.getUserId()));
        } else {
            //删除评论，只能通过id删除
            return toAjax(bizCommentService.deleteBizCommentByCommentId(comment.getCommentId()));
        }
    }

    /**
     * 新增打卡
     */
    @Log(title = "打卡", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizArticle bizArticle) {
        bizArticle.setStatus("0");
        return toAjax(bizArticleService.insertBizArticle(bizArticle));
    }

    /**
     * 上次打卡附件
     */
    @Log(title = "打卡附件", businessType = BusinessType.INSERT)
    @Anonymous
    @PostMapping("/uploadFile")
    public AjaxResult add(@RequestParam("file") MultipartFile multipartFile) {
        OssClient ossClient = ossFactory.instance();
        try {
            UploadResult result = ossClient.upload(multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename(), multipartFile.getSize(), "application/octet-stream");
            return success(result);
        } catch (IOException e) {
            logger.error("上传失败", e);
            return error(e.getMessage());
        }
    }

    /**
     * 修改打卡
     */
    @Log(title = "打卡", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizArticle bizArticle) {
        return toAjax(bizArticleService.updateBizArticle(bizArticle));
    }

    /**
     * 删除打卡
     */
    @Log(title = "打卡", businessType = BusinessType.DELETE)
    @DeleteMapping("/{articleIds}")
    public AjaxResult remove(@PathVariable Long[] articleIds) {
        return toAjax(bizArticleService.deleteBizArticleByArticleIds(articleIds));
    }
}
