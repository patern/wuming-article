package com.wuming.article.mapper;

import java.util.List;
import com.wuming.article.domain.BizUser;

/**
 * 打卡用户Mapper接口
 * 
 * @author wuming
 * @date 2025-05-28
 */
public interface BizUserMapper 
{
    /**
     * 查询打卡用户
     * 
     * @param userId 打卡用户主键
     * @return 打卡用户
     */
    public BizUser selectBizUserByUserId(Long userId);

    /**
     * 查询打卡用户列表
     * 
     * @param bizUser 打卡用户
     * @return 打卡用户集合
     */
    public List<BizUser> selectBizUserList(BizUser bizUser);

    /**
     * 新增打卡用户
     * 
     * @param bizUser 打卡用户
     * @return 结果
     */
    public int insertBizUser(BizUser bizUser);

    /**
     * 修改打卡用户
     * 
     * @param bizUser 打卡用户
     * @return 结果
     */
    public int updateBizUser(BizUser bizUser);

    /**
     * 删除打卡用户
     * 
     * @param userId 打卡用户主键
     * @return 结果
     */
    public int deleteBizUserByUserId(Long userId);

    /**
     * 批量删除打卡用户
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizUserByUserIds(Long[] userIds);
}
