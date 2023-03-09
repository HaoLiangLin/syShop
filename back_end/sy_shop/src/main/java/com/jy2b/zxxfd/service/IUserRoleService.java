package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.UserRole;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 用户角色业务接口
 */
@Transactional
public interface IUserRoleService extends IService<UserRole> {
    /**
     * 根据角色id查询用户
     * @param roleId 角色id
     * @return ResultVO
     */
    ResultVO selectUserByRoleId(Long roleId);

    /**
     * 根据用户id查询角色
     * @param userId 用户id
     * @return ResultVO
     */
    ResultVO selectRoleByUserId(Long userId);

    /**
     * 删除用户角色
     * @param userId 用户Id
     * @return ResultVO
     */
    ResultVO delUserByRoleId(Long userId);

    /**
     * 修改用户角色
     * @param userId 用户id
     * @param roleId 角色id
     * @return ResultVO
     */
    ResultVO updateUserRole(Long userId, Long roleId);

    /**
     * 新增用户角色
     * @param userId 用户id
     * @param roleId 角色id
     * @return ResultVO
     */
    ResultVO saveUserRole(Long userId, Long roleId);
}
