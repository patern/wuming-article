package com.wuming.article.service;

import java.util.List;
import com.wuming.article.domain.BizComment;
import com.wuming.article.dto.BizCommentCountDto;
import com.wuming.article.dto.BizCommentQuery;

/**
 * 打卡评论Service接口
 * 
 * @author patern
 * @date 2025-05-27
 */
public interface IBizCommentService 
{
    /**
     * 查询打卡评论
     * 
     * @param commentId 打卡评论主键
     * @return 打卡评论
     */
    public BizComment selectBizCommentByCommentId(Long commentId);

    /**
     * 查询打卡评论列表
     * 
     * @param bizComment 打卡评论
     * @return 打卡评论集合
     */
    public List<BizComment> selectBizCommentList(BizComment bizComment);
    /**
     * 查询打卡评论列表
     *
     * @param bizComment 打卡评论
     * @return 打卡评论集合
     */
    public List<BizComment> selectBizComment(BizCommentQuery bizComment);
    /**
     * 查询打卡评论条数
     *
     * @param bizComment 打卡评论
     * @return 打卡评论集合
     */
    public List<BizCommentCountDto> selectBizCommentCount(BizCommentQuery bizComment);
    /**
     * 新增打卡评论
     * 
     * @param bizComment 打卡评论
     * @return 结果
     */
    public int insertBizComment(BizComment bizComment);

    /**
     * 修改打卡评论
     * 
     * @param bizComment 打卡评论
     * @return 结果
     */
    public int updateBizComment(BizComment bizComment);

    /**
     * 批量删除打卡评论
     * 
     * @param commentIds 需要删除的打卡评论主键集合
     * @return 结果
     */
    public int deleteBizCommentByCommentIds(Long[] commentIds);

    /**
     * 删除打卡评论信息
     * 
     * @param commentId 打卡评论主键
     * @return 结果
     */
    public int deleteBizCommentByCommentId(Long commentId);

    /**
     * 根据用户id删除评论或点赞
     * @param userId
     */
    public int deleteByUserId(Long userId,Long articleId);
}
