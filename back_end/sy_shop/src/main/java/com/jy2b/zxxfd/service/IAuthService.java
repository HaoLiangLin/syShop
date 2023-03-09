package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.Auth;
import com.jy2b.zxxfd.domain.dto.AuthManageDTO;
import com.jy2b.zxxfd.domain.dto.AuthSaveDTO;
import com.jy2b.zxxfd.domain.dto.AuthUpdateDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 权限业务接口
 */
@Transactional
public interface IAuthService extends IService<Auth> {
    /**
     * 查询权限标签
     * @return ResultVO
     */
    ResultVO labelList();

    /**
     * 根据权限标签获取权限名称
     * @return ResultVO
     */
    ResultVO nameByLabelList(String label);

    /**
     * 查询权限
     * @param page 页码
     * @param size 数量
     * @param authManageDTO 查询权限条件
     * @return ResultVO
     */
    ResultVO findAuth(Integer page, Integer size, AuthManageDTO authManageDTO);

    /**
     * 修改权限
     * @param authUpdateDTO 修改权限条件
     * @return ResultVO
     */
    ResultVO updateAuth(AuthUpdateDTO authUpdateDTO);

    /**
     * 新增权限
     * @param authSaveDTO 新增权限条件
     * @return ResultVO
     */
    ResultVO saveAuth(AuthSaveDTO authSaveDTO);

    /**
     * 删除权限
     * @param id 权限id
     * @return ResultVO
     */
    ResultVO delAuth(Long id);
}
