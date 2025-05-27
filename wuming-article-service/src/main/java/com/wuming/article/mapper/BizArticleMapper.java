package com.wuming.article.mapper;

import java.util.List;
import com.wuming.article.domain.BizArticle;

/**
 * 文章Mapper接口
 * 
 * @author patern
 * @date 2025-05-27
 */
public interface BizArticleMapper 
{
    /**
     * 查询文章
     * 
     * @param articleId 文章主键
     * @return 文章
     */
    public BizArticle selectBizArticleByArticleId(Long articleId);

    /**
     * 查询文章列表
     * 
     * @param bizArticle 文章
     * @return 文章集合
     */
    public List<BizArticle> selectBizArticleList(BizArticle bizArticle);

    /**
     * 新增文章
     * 
     * @param bizArticle 文章
     * @return 结果
     */
    public int insertBizArticle(BizArticle bizArticle);

    /**
     * 修改文章
     * 
     * @param bizArticle 文章
     * @return 结果
     */
    public int updateBizArticle(BizArticle bizArticle);

    /**
     * 删除文章
     * 
     * @param articleId 文章主键
     * @return 结果
     */
    public int deleteBizArticleByArticleId(Long articleId);

    /**
     * 批量删除文章
     * 
     * @param articleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizArticleByArticleIds(Long[] articleIds);
}
