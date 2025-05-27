package com.wuming.article.mapper;

import java.util.List;
import com.wuming.article.domain.BizUser;

/**
 * 用户Mapper接口
 * 
 * @author patern
 * @date 2025-05-27
 */
public interface BizUserMapper 
{
    /**
     * 查询用户
     * 
     * @param userId 用户主键
     * @return 用户
     */
    public BizUser selectBizUserByUserId(Long userId);

    /**
     * 查询用户列表
     * 
     * @param bizUser 用户
     * @return 用户集合
     */
    public List<BizUser> selectBizUserList(BizUser bizUser);

    /**
     * 新增用户
     * 
     * @param bizUser 用户
     * @return 结果
     */
    public int insertBizUser(BizUser bizUser);

    /**
     * 修改用户
     * 
     * @param bizUser 用户
     * @return 结果
     */
    public int updateBizUser(BizUser bizUser);

    /**
     * 删除用户
     * 
     * @param userId 用户主键
     * @return 结果
     */
    public int deleteBizUserByUserId(Long userId);

    /**
     * 批量删除用户
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizUserByUserIds(Long[] userIds);
}
