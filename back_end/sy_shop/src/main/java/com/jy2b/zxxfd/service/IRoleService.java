package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.Role;
import com.jy2b.zxxfd.domain.dto.RoleManageDTO;
import com.jy2b.zxxfd.domain.dto.RoleSaveDTO;
import com.jy2b.zxxfd.domain.dto.RoleUpdateDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IRoleService extends IService<Role> {

    /**
     * 查询角色标签
     * @return ResultVO
     */
    ResultVO labelList();

    /**
     * 根据角色标签查询角色名称
     * @param label 角色标签
     * @return ResultVO
     */
    ResultVO nameByLabelList(String label);

    /**
     * 查询角色
     * @param page 页码
     * @param size 数量
     * @param roleManageDTO 查询角色条件信息
     * @return ResultVO
     */
    ResultVO findRole(Integer page, Integer size, RoleManageDTO roleManageDTO);

    /**
     * 修改角色
     * @param roleUpdateDTO 修改角色信息
     * @return ResultVO
     */
    ResultVO updateRole(RoleUpdateDTO roleUpdateDTO);

    /**
     * 新增角色
     * @param roleSaveDTO 新增角色信息
     * @return RoleSaveDTO
     */
    ResultVO saveRole(RoleSaveDTO roleSaveDTO);

    /**
     * 删除角色
     * @param id 角色ID
     * @return ResultVO
     */
    ResultVO delRole(Long id);
}
