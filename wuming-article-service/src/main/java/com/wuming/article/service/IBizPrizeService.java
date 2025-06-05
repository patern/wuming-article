package com.wuming.article.service;

import java.util.List;
import com.wuming.article.domain.BizPrize;

/**
 * 用户提现Service接口
 * 
 * @author wuming
 * @date 2025-06-05
 */
public interface IBizPrizeService 
{
    /**
     * 查询用户提现
     * 
     * @param prizeId 用户提现主键
     * @return 用户提现
     */
    public BizPrize selectBizPrizeByPrizeId(Long prizeId);

    /**
     * 查询用户提现列表
     * 
     * @param bizPrize 用户提现
     * @return 用户提现集合
     */
    public List<BizPrize> selectBizPrizeList(BizPrize bizPrize);

    /**
     * 新增用户提现
     * 
     * @param bizPrize 用户提现
     * @return 结果
     */
    public int insertBizPrize(BizPrize bizPrize);

    /**
     * 修改用户提现
     * 
     * @param bizPrize 用户提现
     * @return 结果
     */
    public int updateBizPrize(BizPrize bizPrize);

    /**
     * 批量删除用户提现
     * 
     * @param prizeIds 需要删除的用户提现主键集合
     * @return 结果
     */
    public int deleteBizPrizeByPrizeIds(Long[] prizeIds);

    /**
     * 删除用户提现信息
     * 
     * @param prizeId 用户提现主键
     * @return 结果
     */
    public int deleteBizPrizeByPrizeId(Long prizeId);
}
