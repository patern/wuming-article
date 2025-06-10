package com.wuming.web.controller.front;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Sets;
import com.wuming.article.domain.BizArticle;
import com.wuming.article.domain.BizComment;
import com.wuming.article.domain.BizUser;
import com.wuming.article.dto.*;
import com.wuming.article.service.IBizArticleService;
import com.wuming.article.service.IBizCommentService;
import com.wuming.article.service.IBizUserService;
import com.wuming.common.annotation.Anonymous;
import com.wuming.common.annotation.Log;
import com.wuming.common.constant.CacheConstants;
import com.wuming.common.core.controller.BaseController;
import com.wuming.common.core.domain.AjaxResult;
import com.wuming.common.core.domain.model.LoginUser;
import com.wuming.common.core.page.TableDataInfo;
import com.wuming.common.core.redis.RedisCache;
import com.wuming.common.enums.BusinessType;
import com.wuming.common.oss.constant.OssConstant;
import com.wuming.common.oss.core.OssClient;
import com.wuming.common.oss.entity.UploadResult;
import com.wuming.common.oss.factory.OssFactory;
import com.wuming.common.utils.SecurityUtils;
import com.wuming.common.utils.StringUtils;
import com.wuming.common.utils.file.FileUtils;
import com.wuming.common.utils.poi.ExcelUtil;
import com.wuming.system.service.ISysConfigService;
import com.wuming.system.service.ISysOperLogService;
import com.wuming.web.controller.front.vo.BizArticleVo;
import com.wuming.web.controller.front.vo.BizSumInfoVo;
import com.wuming.web.controller.front.vo.UserPrizeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private ISysOperLogService operLogService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询打卡列表
     * 查询用户自己的设置 userId，并且状态可以是空，查询所有
     * 查询详情的 无须设置userId, 但是需要设置状态是 0，表示查询状态正常的，没有被用户自己作废，或者被管理员下架的
     * 小程序点击评论和点赞的时候必须获取当前用户信息，如果判断获取的用户id和评论和点赞的userId有当前用户，就表示为点亮状态，否则为未点亮装
     * 并且显示评论总条数（只显示前5条，更多的需要点击详情查看，进入详情就需要登陆），点赞数量
     */
    @GetMapping("/list")
    @Anonymous
    public TableDataInfo list(BizArticleQuery bizArticle) {
        startPage();
        List<BizArticle> list = bizArticleService.selectBizArticleList(bizArticle);
        TableDataInfo tableDataInfo = getDataTable(list);
        if (CollectionUtils.isEmpty(list)) {
            return getDataTable(list);
        }

        List<Long> ids = list.stream().map(BizArticle::getArticleId).collect(Collectors.toList());
        BizCommentQuery query = new BizCommentQuery();
        query.setArticleIds(ids);
        query.setStatus("0");
        List<BizCommentCountDto> comments = bizCommentService.selectBizCommentCount(query);

        Set<Long> userIds = list.stream().map(BizArticle::getUserId).collect(Collectors.toSet());
        BizUserQuery query1 = new BizUserQuery();
        query1.setUserIds(userIds);
//        query1.setStatus("0");
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
            if ("1".equals(vo.getArticleType())){
                vo.setScreenUrl(vo.getArticleAttaUrl()+"?x-oss-process=video/snapshot,t_1000,f_jpg,w_800,h_600,m_fast");
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
        tableDataInfo.setRows(subComments);
        return tableDataInfo;
    }

    /**
     * 查询打卡列表
     * 查询用户自己的设置 userId，并且状态可以是空，查询所有
     * 查询详情的 无须设置userId, 但是需要设置状态是 0，表示查询状态正常的，没有被用户自己作废，或者被管理员下架的
     * 小程序点击评论和点赞的时候必须获取当前用户信息，如果判断获取的用户id和评论和点赞的userId有当前用户，就表示为点亮状态，否则为未点亮装
     * 并且显示评论总条数（只显示前5条，更多的需要点击详情查看，进入详情就需要登陆），点赞数量
     */
    @GetMapping("/ranking/list")
    @Anonymous
    public TableDataInfo rankingList(BizArticleQuery bizArticle) {
        startPage();
        List<BizArticleCountDto> list = bizArticleService.selectBizArticleSumList(bizArticle);
        TableDataInfo tableDataInfo = getDataTable(list);
        if (CollectionUtils.isEmpty(list)) {
            return tableDataInfo;
        }

        Set<Long> userIds = list.stream().map(BizArticleCountDto::getUserId).collect(Collectors.toSet());
        BizUserQuery query1 = new BizUserQuery();
        query1.setUserIds(userIds);
//        query1.setStatus("0");
        List<BizUser> users = bizUserService.selectBizUser(query1);
        Map<Long, BizUser> userMap = users.stream().collect(Collectors.toMap(BizUser::getUserId, v -> v));

        List<BizArticleVo> subComments = Lists.newArrayList();

        for (BizArticleCountDto article : list) {
            BizArticleVo vo = new BizArticleVo();
            BeanUtils.copyProperties(article, vo);
            BizUser u = userMap.get(article.getUserId());
            if (null != u) {
                vo.setAvatarUrl(u.getAvatarUrl());
                vo.setUserName(u.getNickName());
                vo.setSchoolName(u.getSchoolName());
            }
            subComments.add(vo);
        }
        tableDataInfo.setRows(subComments);
        return tableDataInfo;
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
        Set<Long> userIds = Sets.newHashSet();
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
                    && e.getUserId().equals(SecurityUtils.getLoginUser().getUser().getUserId())).collect(Collectors.toList()).size() > 0);
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
     * 点赞或者评论
     * userId，articleId，status 0:正常，1取消点赞，或者删除评论， commentType：评论类型（1点赞 2评论）
     * 如果是删除评论或者点赞，则需要传递评论的id commentId，删除自己删除自己的评论，或者取消自己的点赞
     */
    @PostMapping(value = "/sumInfo")
    @Anonymous
    @Log(title = "统计信息", businessType = BusinessType.INSERT)
    public AjaxResult sumInfo() {
        Long userCount = bizUserService.countUser();
        Long articleCount =  bizArticleService.countArticle();
        Long accessCount = operLogService.countWxAcessCount();
        BizSumInfoVo sumInfoVo = new BizSumInfoVo();
        sumInfoVo.setAccessCount(accessCount);
        sumInfoVo.setUserCount(userCount);
        sumInfoVo.setArticleCount(articleCount);
        return success(sumInfoVo);
    }


    /**
     * 取消点赞或者评论
     */
    @DeleteMapping(value = "/comment")
    @Log(title = "评论/点赞", businessType = BusinessType.DELETE)
    public AjaxResult cancelUpVote(@RequestBody BizComment comment) {
        if (null == comment.getCommentId()) {
            if (null==comment.getArticleId()){
                return error("取消点赞打卡不能为空");
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            return toAjax(bizCommentService.deleteByUserId(loginUser.getUserId(),comment.getArticleId()));
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
        bizArticleService.insertBizArticle(bizArticle);
        return success(bizArticle.getArticleId());
    }

    /**
     * 上次打卡附件
     */
    @Log(title = "打卡附件", businessType = BusinessType.INSERT)
    @PostMapping("/uploadFile")
    public AjaxResult add(@RequestParam("file") MultipartFile multipartFile) {
        OssClient ossClient = ossFactory.instance();
        try {
            UploadResult result = ossClient.upload(multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename(), multipartFile.getSize(), FileUtils.getMimeType(multipartFile.getOriginalFilename()));
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
    @Log(title = "作废", businessType = BusinessType.DELETE)
    @DeleteMapping("/{articleIds}")
    public AjaxResult remove(@PathVariable Long[] articleIds) {
        return toAjax(bizArticleService.deleteBizArticleByArticleIds(articleIds));
    }

    /**
     * 查询红包配置
     */
//    @PostMapping("/getPrizeConfig")
    public List<BigDecimal> getPrizeConfig() {
        BizArticleQuery query = new BizArticleQuery();
        query.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        query.setStatus("0");
        List<BizArticle> articles = bizArticleService.selectBizArticleList(query);
        //默认金额配置
        String configKey = configService.selectConfigByKey(CacheConstants.PRIZE_CONFIG_KEY);
        if (CollectionUtils.isNotEmpty(articles)) {
//            List<BizArticle> prizeArticle = articles.stream().filter(e ->
//                    e.getCreateTime().after(DateUtil.beginOfDay(new Date()))
//                            && e.getCreateTime().before(DateUtil.offsetDay(new Date(), 1)) &&
//                            null != e.getPrize() && e.getPrize().
//                            compareTo(new BigDecimal(0)) > 0).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(prizeArticle)) {
//                throw new RuntimeException("今日已获得奖励!");
//            }
            String level = configService.selectConfigByKey(CacheConstants.PRIZE_LEVEL_KEY);
            //不同金额的个数
            Map<String, String> levelMap = JSONObject.parseObject(level, Map.class);

            BigDecimal mBig = new BigDecimal(levelMap.get("M"));
            BigDecimal lBig = new BigDecimal(levelMap.get("L"));
            List<BizArticle> lastMax = articles.stream().filter(e ->null != e.getPrize() && e.getPrize().
                            compareTo(mBig) >= 0 && e.getPrize().
                    compareTo(lBig) < 0).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(lastMax)){
                //上次累计10天打卡获取奖金后的累计天数
                BizArticle lastPrize = lastMax.get(0);
                BizArticleQuery query1 = new BizArticleQuery();
                query1.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                query1.setCreateBeginTime(lastPrize.getCreateTime());
                query1.setStatus("0");
                List<BizArticleCountDto> countDtos = bizArticleService.selectBizArticleSumList(query1);
                if (CollectionUtils.isNotEmpty(countDtos) && countDtos.get(0).getReportDay()>=10){
                    configKey = configService.selectConfigByKey(CacheConstants.PRIZE_TEN_CONFIG_KEY);
                }
            }else if (articles.size()>=10){
                BizArticleQuery query1 = new BizArticleQuery();
                query1.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                query1.setStatus("0");
                List<BizArticleCountDto> countDtos = bizArticleService.selectBizArticleSumList(query1);
                if (CollectionUtils.isNotEmpty(countDtos) && countDtos.get(0).getReportDay()>=10){
                    configKey = configService.selectConfigByKey(CacheConstants.PRIZE_TEN_CONFIG_KEY);
                }
            }

            List<BizArticle> lastLMax = articles.stream().filter(e ->null != e.getPrize() && e.getPrize().
                    compareTo(lBig) >= 0).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(lastLMax)){
                //上次累计25天打卡获取奖金后的累计天数
                BizArticle lastPrize = lastLMax.get(0);
                BizArticleQuery query1 = new BizArticleQuery();
                query1.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                query1.setCreateBeginTime(lastPrize.getCreateTime());
                query1.setStatus("0");
                List<BizArticleCountDto> countDtos = bizArticleService.selectBizArticleSumList(query1);
                if (CollectionUtils.isNotEmpty(countDtos) && countDtos.get(0).getReportDay()>=25){
                    configKey = configService.selectConfigByKey(CacheConstants.PRIZE_25_CONFIG_KEY);
                }
            }else {
                BizArticleQuery query1 = new BizArticleQuery();
                query1.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                query1.setStatus("0");
                List<BizArticleCountDto> countDtos = bizArticleService.selectBizArticleSumList(query1);
                if (CollectionUtils.isNotEmpty(countDtos) && countDtos.get(0).getReportDay()>=25){
                    configKey = configService.selectConfigByKey(CacheConstants.PRIZE_25_CONFIG_KEY);
                }
            }
        }
        //不同金额的个数
        Map<String, Integer> map = JSONObject.parseObject(configKey, Map.class);
        List<BigDecimal> prize = new ArrayList<>();
        for (String key : map.keySet()) {
            Integer count = map.get(key);
            for (int i = 0; i < count; i++) {
                if (key.contains("-")) {
                    double a = Double.valueOf(key.split("-")[0]);
                    double b = Double.valueOf(key.split("-")[1]);
                    double randomDouble = a + (b - a) * ThreadLocalRandom.current().nextDouble();
                    prize.add(new BigDecimal(randomDouble).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    prize.add(new BigDecimal(key).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
//        redisCache.setCacheObject(CacheConstants.PRIZE_USER_PRICE_ALL + ":" + SecurityUtils.getLoginUser().getUser().getUserId(), prize);
        return prize;
    }

    @PostMapping("/doPrize")
    public AjaxResult doPrize(@RequestBody UserPrizeVo prizeVo) {
//        if (null == prizeVo.getPrize()) {
//            return error("奖金金额不能为空");
//        }
        if (null == prizeVo.getUserId()) {
            return error("用户不能为空");
        }

        if (null == prizeVo.getArticleId()) {
            return error("打卡不能为空");
        }
        if (!prizeVo.getUserId().equals(SecurityUtils.getLoginUser().getUser().getUserId())) {
            return error("用户不匹配");
        }

//        List<BigDecimal> prize = redisCache.getCacheObject(CacheConstants.PRIZE_USER_PRICE_ALL +
//                ":" + SecurityUtils.getLoginUser().getUser().getUserId());
//        if (!prize.contains(prizeVo.getPrize())) {
//            return error("金额非法");
//        }
        BizArticle article = bizArticleService.selectBizArticleByArticleId(prizeVo.getArticleId());
        if (null == article) {
            return error("未找到对应打卡记录");
        } else if (null != article.getPrize() && article.getPrize().compareTo(BigDecimal.ZERO) > 0) {
            return error("打卡已抽奖!");
        }

        BizArticleQuery query = new BizArticleQuery();
        query.setCreateBeginTime(DateUtil.beginOfDay(new Date()));
        query.setCreateEndTime(DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), 1)));
        query.setUserId(prizeVo.getUserId());
        List<BizArticle> articles = bizArticleService.selectBizArticleList(query);
        if (CollectionUtils.isNotEmpty(articles)) {
            List<BizArticle> hasPrized = articles.stream().filter(e -> null != e.getPrize() && e.getPrize().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(hasPrized)) {
                return error("今日已获得奖励!");
            }
        }
        prizeVo.setPrize(getPrizeConfig().get(0));
        //清理抽奖配置
//        redisCache.deleteObject(CacheConstants.PRIZE_USER_PRICE_ALL + ":" + SecurityUtils.getLoginUser().getUser().getUserId());
        BizArticle upArticle = new BizArticleVo();
        upArticle.setArticleId(article.getArticleId());
        upArticle.setPrize(prizeVo.getPrize());
        bizArticleService.updateBizArticle(upArticle);
        return success(prizeVo.getPrize());
    }
}
