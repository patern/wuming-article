package com.wuming.article.mapper;

import java.util.List;
import com.wuming.article.domain.BizComment;

/**
 * 文章评论Mapper接口
 * 
 * @author patern
 * @date 2025-05-27
 */
public interface BizCommentMapper 
{
    /**
     * 查询文章评论
     * 
     * @param commentId 文章评论主键
     * @return 文章评论
     */
    public BizComment selectBizCommentByCommentId(Long commentId);

    /**
     * 查询文章评论列表
     * 
     * @param bizComment 文章评论
     * @return 文章评论集合
     */
    public List<BizComment> selectBizCommentList(BizComment bizComment);

    /**
     * 新增文章评论
     * 
     * @param bizComment 文章评论
     * @return 结果
     */
    public int insertBizComment(BizComment bizComment);

    /**
     * 修改文章评论
     * 
     * @param bizComment 文章评论
     * @return 结果
     */
    public int updateBizComment(BizComment bizComment);

    /**
     * 删除文章评论
     * 
     * @param commentId 文章评论主键
     * @return 结果
     */
    public int deleteBizCommentByCommentId(Long commentId);

    /**
     * 批量删除文章评论
     * 
     * @param commentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizCommentByCommentIds(Long[] commentIds);
}
