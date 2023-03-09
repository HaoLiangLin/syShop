package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.UserInfo;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 用户信息业务接口
 */
@Transactional
public interface IUserInfoService extends IService<UserInfo> {
    /**
     * 用户：获取用户信息
     * @return ResultVo
     */
    ResultVO queryInfoMe();

    /**
     * 用户：修改用户信息
     * @param userInfo 用户信息
     * @return ResultVO
     */
    ResultVO updateInfo(UserInfo userInfo);

    /**
     * 管理员：根据用户ID查询用户信息
     * @param userId 用户ID
     * @return ResultVO
     */
    ResultVO queryUserInfoById(Long userId);

    /**
     * 管理员：修改用户信息
     * @param userInfo 用户信息
     * @return ResultVo
     */
    ResultVO updateInfoAdmin(UserInfo userInfo);
}
