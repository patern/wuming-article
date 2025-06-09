package com.wuming.article.service.impl;

import java.util.List;

import com.google.common.collect.Sets;
import com.wuming.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wuming.article.mapper.BizPrizeMapper;
import com.wuming.article.domain.BizPrize;
import com.wuming.article.service.IBizPrizeService;

/**
 * 用户提现Service业务层处理
 * 
 * @author wuming
 * @date 2025-06-05
 */
@Service
public class BizPrizeServiceImpl implements IBizPrizeService 
{
    @Autowired
    private BizPrizeMapper bizPrizeMapper;

    /**
     * 查询用户提现
     * 
     * @param prizeId 用户提现主键
     * @return 用户提现
     */
    @Override
    public BizPrize selectBizPrizeByPrizeId(Long prizeId)
    {
        return bizPrizeMapper.selectBizPrizeByPrizeId(prizeId);
    }

    /**
     * 查询用户提现列表
     * 
     * @param bizPrize 用户提现
     * @return 用户提现
     */
    @Override
    public List<BizPrize> selectBizPrizeList(BizPrize bizPrize)
    {
        return bizPrizeMapper.selectBizPrizeList(bizPrize);
    }

    /**
     * 查询状态未完成的单据
     * @return
     */
    public List<BizPrize>  selectUnCompleteBill(BizPrize bizPrize){

        bizPrize.setStatusSet(Sets.newHashSet("ACCEPTED","PROCESSING","WAIT_USER_CONFIRM","TRANSFERING","CANCELING"));
        return bizPrizeMapper.selectUnCompleteBill(bizPrize);
    }

    /**
     * 新增用户提现
     * 
     * @param bizPrize 用户提现
     * @return 结果
     */
    @Override
    public int insertBizPrize(BizPrize bizPrize)
    {
        bizPrize.setCreateTime(DateUtils.getNowDate());
        return bizPrizeMapper.insertBizPrize(bizPrize);
    }

    /**
     * 修改用户提现
     * 
     * @param bizPrize 用户提现
     * @return 结果
     */
    @Override
    public int updateBizPrize(BizPrize bizPrize)
    {
        bizPrize.setUpdateTime(DateUtils.getNowDate());
        return bizPrizeMapper.updateBizPrize(bizPrize);
    }

    /**
     * 批量删除用户提现
     * 
     * @param prizeIds 需要删除的用户提现主键
     * @return 结果
     */
    @Override
    public int deleteBizPrizeByPrizeIds(Long[] prizeIds)
    {
        return bizPrizeMapper.deleteBizPrizeByPrizeIds(prizeIds);
    }

    /**
     * 删除用户提现信息
     * 
     * @param prizeId 用户提现主键
     * @return 结果
     */
    @Override
    public int deleteBizPrizeByPrizeId(Long prizeId)
    {
        return bizPrizeMapper.deleteBizPrizeByPrizeId(prizeId);
    }
}
