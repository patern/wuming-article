package com.wuming.article.service;

import java.util.List;

import com.wuming.article.domain.BizComment;
import com.wuming.article.domain.BizUser;
import com.wuming.article.dto.BizCommentQuery;
import com.wuming.article.dto.BizUserQuery;

/**
 * 打卡用户Service接口
 * 
 * @author wuming
 * @date 2025-05-28
 */
public interface IBizUserService 
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

    List<BizUser> selectBizUser(BizUserQuery bizUser);

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
     * 批量删除打卡用户
     * 
     * @param userIds 需要删除的打卡用户主键集合
     * @return 结果
     */
    public int deleteBizUserByUserIds(Long[] userIds);

    /**
     * 删除打卡用户信息
     * 
     * @param userId 打卡用户主键
     * @return 结果
     */
    public int deleteBizUserByUserId(Long userId);

}
