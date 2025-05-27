package com.wuming.article.service.impl;

import java.util.List;
import com.wuming.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wuming.article.mapper.BizCommentMapper;
import com.wuming.article.domain.BizComment;
import com.wuming.article.service.IBizCommentService;

/**
 * 文章评论Service业务层处理
 * 
 * @author patern
 * @date 2025-05-27
 */
@Service
public class BizCommentServiceImpl implements IBizCommentService 
{
    @Autowired
    private BizCommentMapper bizCommentMapper;

    /**
     * 查询文章评论
     * 
     * @param commentId 文章评论主键
     * @return 文章评论
     */
    @Override
    public BizComment selectBizCommentByCommentId(Long commentId)
    {
        return bizCommentMapper.selectBizCommentByCommentId(commentId);
    }

    /**
     * 查询文章评论列表
     * 
     * @param bizComment 文章评论
     * @return 文章评论
     */
    @Override
    public List<BizComment> selectBizCommentList(BizComment bizComment)
    {
        return bizCommentMapper.selectBizCommentList(bizComment);
    }

    /**
     * 新增文章评论
     * 
     * @param bizComment 文章评论
     * @return 结果
     */
    @Override
    public int insertBizComment(BizComment bizComment)
    {
        bizComment.setCreateTime(DateUtils.getNowDate());
        return bizCommentMapper.insertBizComment(bizComment);
    }

    /**
     * 修改文章评论
     * 
     * @param bizComment 文章评论
     * @return 结果
     */
    @Override
    public int updateBizComment(BizComment bizComment)
    {
        bizComment.setUpdateTime(DateUtils.getNowDate());
        return bizCommentMapper.updateBizComment(bizComment);
    }

    /**
     * 批量删除文章评论
     * 
     * @param commentIds 需要删除的文章评论主键
     * @return 结果
     */
    @Override
    public int deleteBizCommentByCommentIds(Long[] commentIds)
    {
        return bizCommentMapper.deleteBizCommentByCommentIds(commentIds);
    }

    /**
     * 删除文章评论信息
     * 
     * @param commentId 文章评论主键
     * @return 结果
     */
    @Override
    public int deleteBizCommentByCommentId(Long commentId)
    {
        return bizCommentMapper.deleteBizCommentByCommentId(commentId);
    }
}
