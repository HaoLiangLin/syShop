package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.RoleAuth;
import com.jy2b.zxxfd.domain.dto.RoleAuthDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IRoleAuthService extends IService<RoleAuth> {
    /**
     * 根据角色id查询权限
     * @param roleId 角色ID
     * @return ResultVO
     */
    ResultVO selectAuthByRoleId(Long roleId);

    /**
     * 根据权限id查询角色
     * @param authId 权限id
     * @return ResultVO
     */
    ResultVO selectRoleByAuthId(Long authId);

    /**
     * 删除角色指定权限
     * @param roleId 角色id
     * @param authId 权限id
     * @return ResultVO
     */
    ResultVO delAuthByRoleId(Long roleId, Long authId);

    /**
     * 根据角色id删除全部权限
     * @param roleId 角色id
     * @return ResultVO
     */
    ResultVO delAllAuthByRoleId(Long roleId);

    /**
     * 根据权限id删除全部角色
     * @param authId 权限id
     * @return ResultVO
     */
    ResultVO delAllRoleByAuthId(Long authId);

    /**
     * 新增角色权限
     * @param roleId 角色Id
     * @param authId 权限Id
     * @return ResultVO
     */
    ResultVO saveRoleAuth(Long roleId, Long authId);

    /**
     * 批量新增角色权限
     * @param roleAuthDTO 新增角色权限信息
     * @return ResultVO
     */
    ResultVO batchSaveRoleAuth(RoleAuthDTO roleAuthDTO);
}
