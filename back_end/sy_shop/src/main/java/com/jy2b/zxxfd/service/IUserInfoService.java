package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserInfo;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IUserInfoService extends IService<UserInfo> {
    /**
     * 用户：获取用户信息
     * @return ResultVo
     */
    ResultVo queryInfoMe();

    /**
     * 用户：修改用户信息
     * @param userInfo 用户信息
     * @return ResultVO
     */
    ResultVo updateInfo(UserInfo userInfo);

    /**
     * 管理员：根据条件查询用户信息
     * @param userInfo 用户信息
     * @return ResultVo
     */
    ResultVo queryInfo(UserInfo userInfo);

    /**
     * 管理员：根据条件分页查询用户信息
     * @param page 页数
     * @param number 条数
     * @param userInfo 用户信息
     * @return ResultVo
     */
    ResultVo queryInfoAll(Integer page, Integer number, UserInfo userInfo);

    /**
     * 管理员：修改用户信息
     * @param userInfo 用户信息
     * @return ResultVo
     */
    ResultVo updateInfoAdmin(UserInfo userInfo);
}
