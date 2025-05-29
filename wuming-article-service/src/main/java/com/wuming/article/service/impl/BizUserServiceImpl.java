package com.wuming.article.service.impl;

import java.util.List;
import com.wuming.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wuming.article.mapper.BizUserMapper;
import com.wuming.article.domain.BizUser;
import com.wuming.article.service.IBizUserService;

/**
 * 打卡用户Service业务层处理
 * 
 * @author wuming
 * @date 2025-05-28
 */
@Service
public class BizUserServiceImpl implements IBizUserService 
{
    @Autowired
    private BizUserMapper bizUserMapper;

    /**
     * 查询打卡用户
     * 
     * @param userId 打卡用户主键
     * @return 打卡用户
     */
    @Override
    public BizUser selectBizUserByUserId(Long userId)
    {
        return bizUserMapper.selectBizUserByUserId(userId);
    }

    /**
     * 查询打卡用户列表
     * 
     * @param bizUser 打卡用户
     * @return 打卡用户
     */
    @Override
    public List<BizUser> selectBizUserList(BizUser bizUser)
    {
        return bizUserMapper.selectBizUserList(bizUser);
    }

    /**
     * 新增打卡用户
     * 
     * @param bizUser 打卡用户
     * @return 结果
     */
    @Override
    public int insertBizUser(BizUser bizUser)
    {
        bizUser.setCreateTime(DateUtils.getNowDate());
        return bizUserMapper.insertBizUser(bizUser);
    }

    /**
     * 修改打卡用户
     * 
     * @param bizUser 打卡用户
     * @return 结果
     */
    @Override
    public int updateBizUser(BizUser bizUser)
    {
        bizUser.setUpdateTime(DateUtils.getNowDate());
        return bizUserMapper.updateBizUser(bizUser);
    }

    /**
     * 批量删除打卡用户
     * 
     * @param userIds 需要删除的打卡用户主键
     * @return 结果
     */
    @Override
    public int deleteBizUserByUserIds(Long[] userIds)
    {
        return bizUserMapper.deleteBizUserByUserIds(userIds);
    }

    /**
     * 删除打卡用户信息
     * 
     * @param userId 打卡用户主键
     * @return 结果
     */
    @Override
    public int deleteBizUserByUserId(Long userId)
    {
        return bizUserMapper.deleteBizUserByUserId(userId);
    }
}
