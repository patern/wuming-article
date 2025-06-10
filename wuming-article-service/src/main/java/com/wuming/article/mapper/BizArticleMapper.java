package com.wuming.article.mapper;

import java.util.List;
import com.wuming.article.domain.BizArticle;
import com.wuming.article.dto.BizArticleCountDto;
import com.wuming.article.dto.BizArticleQuery;

/**
 * 打卡Mapper接口
 * 
 * @author patern
 * @date 2025-05-27
 */
public interface BizArticleMapper 
{
    /**
     * 查询打卡
     * 
     * @param articleId 打卡主键
     * @return 打卡
     */
    public BizArticle selectBizArticleByArticleId(Long articleId);

    /**
     * 查询打卡列表
     * 
     * @param bizArticle 打卡
     * @return 打卡集合
     */
    public List<BizArticle> selectBizArticleList(BizArticleQuery bizArticle);

    /**
     * 统计打卡列表
     *
     * @param bizArticle 打卡
     * @return 打卡集合
     */
    public List<BizArticleCountDto> selectBizArticleRankingList(BizArticleQuery bizArticle);

    /**
     * 新增打卡
     * 
     * @param bizArticle 打卡
     * @return 结果
     */
    public int insertBizArticle(BizArticle bizArticle);

    /**
     * 修改打卡
     * 
     * @param bizArticle 打卡
     * @return 结果
     */
    public int updateBizArticle(BizArticle bizArticle);

    /**
     * 删除打卡
     * 
     * @param articleId 打卡主键
     * @return 结果
     */
    public int deleteBizArticleByArticleId(Long articleId);

    /**
     * 批量删除打卡
     * 
     * @param articleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizArticleByArticleIds(Long[] articleIds);

    Long countArticle();
}
