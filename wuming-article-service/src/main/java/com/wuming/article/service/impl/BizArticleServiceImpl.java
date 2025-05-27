package com.wuming.article.service.impl;

import java.util.List;
import com.wuming.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wuming.article.mapper.BizArticleMapper;
import com.wuming.article.domain.BizArticle;
import com.wuming.article.service.IBizArticleService;

/**
 * 文章Service业务层处理
 * 
 * @author patern
 * @date 2025-05-27
 */
@Service
public class BizArticleServiceImpl implements IBizArticleService 
{
    @Autowired
    private BizArticleMapper bizArticleMapper;

    /**
     * 查询文章
     * 
     * @param articleId 文章主键
     * @return 文章
     */
    @Override
    public BizArticle selectBizArticleByArticleId(Long articleId)
    {
        return bizArticleMapper.selectBizArticleByArticleId(articleId);
    }

    /**
     * 查询文章列表
     * 
     * @param bizArticle 文章
     * @return 文章
     */
    @Override
    public List<BizArticle> selectBizArticleList(BizArticle bizArticle)
    {
        return bizArticleMapper.selectBizArticleList(bizArticle);
    }

    /**
     * 新增文章
     * 
     * @param bizArticle 文章
     * @return 结果
     */
    @Override
    public int insertBizArticle(BizArticle bizArticle)
    {
        bizArticle.setCreateTime(DateUtils.getNowDate());
        return bizArticleMapper.insertBizArticle(bizArticle);
    }

    /**
     * 修改文章
     * 
     * @param bizArticle 文章
     * @return 结果
     */
    @Override
    public int updateBizArticle(BizArticle bizArticle)
    {
        bizArticle.setUpdateTime(DateUtils.getNowDate());
        return bizArticleMapper.updateBizArticle(bizArticle);
    }

    /**
     * 批量删除文章
     * 
     * @param articleIds 需要删除的文章主键
     * @return 结果
     */
    @Override
    public int deleteBizArticleByArticleIds(Long[] articleIds)
    {
        return bizArticleMapper.deleteBizArticleByArticleIds(articleIds);
    }

    /**
     * 删除文章信息
     * 
     * @param articleId 文章主键
     * @return 结果
     */
    @Override
    public int deleteBizArticleByArticleId(Long articleId)
    {
        return bizArticleMapper.deleteBizArticleByArticleId(articleId);
    }
}
