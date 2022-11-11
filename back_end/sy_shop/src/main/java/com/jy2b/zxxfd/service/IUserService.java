package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.UserInfo;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IUserService extends IService<User> {
    /**
     * 注册：获取验证码
     * @param phone 手机号
     * @return ResultVo
     */
    ResultVo registerCode(String phone);

    /**
     * 用户注册
     * @param register 用户注册信息
     * @return ResultVo
     */
    ResultVo register(RegisterFromDTO register);

    /**
     * 用户登录：发送验证码
     * @param phone 手机号
     * @return ResultVo
     */
    ResultVo sendCode(String phone);

    /**
     * 用户登录
     * @param loginFormDTO 登录信息
     * @return ResultVo
     */
    ResultVo login(LoginFormDTO loginFormDTO);

    /**
     * 用户：修改密码
     * @param pwdFormDTO 修改密码信息
     * @return ResultVo
     */
    ResultVo setPassword(PwdFormDTO pwdFormDTO);

    /**
     * 用户：修改用户头像
     * @param icon 头像地址
     * @return ResultVo
     */
    ResultVo updateIcon(String jwt, String icon);

    /**
     * 用户：查看用户是否未修改过账号
     * @return ResultVo
     */
    ResultVo isNotUpdateUsername();

    /**
     * 用户：修改用户账号
     * @param account 账号
     * @return ResultVo
     */
    ResultVo updateUsername(String account);

    /**
     * 用户：修改用户昵称
     * @param nickname 昵称
     * @return ResultVo
     */
    ResultVo updateNickName(String jwt, String nickname);

    /**
     * 修改手机号：验证手机号
     * @param phone 手机号
     * @return ResultVo
     */
    ResultVo codePhone(String phone);

    /**
     * 用户：修改手机号
     * @param phone 手机号
     * @param code 验证码
     * @return ResultVo
     */
    ResultVo updatePhone(String jwt, String phone, String code);

    /**
     * 用户登出
     * @param token token
     * @return ResultVo
     */
    ResultVo logOut(String token);

    /**
     * 用户：忘记密码：发送验证码
     * @param phone 手机号
     * @return ResultVo
     */
    ResultVo codePassword(String phone);

    /**
     * 用户：忘记密码：验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return ResultVo
     */
    ResultVo checkCodePassword(String phone, String code);

    /**
     * 用户：忘记密码：修改密码
     * @param pwdFormDTO 密码信息
     * @return ResultVo
     */
    ResultVo updatePassword(PwdFormDTO pwdFormDTO);

    /**
     * 管理员：查询用户
     * @param user 查询条件
     * @return ResultVo
     */
    ResultVo queryUser(UserQueryFromDTO user);

    /**
     * 管理员：查询多个用户
     * @param page 页数
     * @param size 数量
     * @param userQueryFromDTO 查询条件
     * @return ResultVO
     */
    ResultVo queryUserList(Integer page, Integer size, UserQueryFromDTO userQueryFromDTO);

    /**
     * 管理员：修改用户
     * @param user 用户信息
     * @return ResultVo
     */
    ResultVo updateUser(User user);

    /**
     * 管理员：删除用户
     * @param id 用户id
     * @return ResultVo
     */
    ResultVo deleteUser(Long id);

    /**
     * 管理员：批量删除用户
     * @param ids 用户id集合
     * @return ResultVo
     */
    ResultVo bulkDeleteUser(List<Long> ids);

    /**
     * 签到
     * @return ResultVo
     */
    ResultVo signIn();

    /**
     * 查询签到记录
     * @return ResultVo
     */
    ResultVo querySignIn();

    /**
     * 获取本月连续签到天数
     * @return ResultVo
     */
    ResultVo queryContinuityDay();
}
