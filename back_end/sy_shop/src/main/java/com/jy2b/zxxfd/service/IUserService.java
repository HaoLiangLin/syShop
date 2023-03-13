package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.UserInfo;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.User;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 林武泰
 * 用户业务接口
 */
@Transactional
public interface IUserService extends IService<User> {
    /**
     * 注册：获取验证码
     * @param phone 手机号
     * @return ResultVo
     */
    ResultVO sendCodeByRegister(String phone);

    /**
     * 用户注册
     * @param registerDTO 用户注册信息
     * @return ResultVo
     */
    ResultVO register(RegisterDTO registerDTO);

    /**
     * 用户登录：发送验证码
     * @param phone 手机号
     * @return ResultVo
     */
    ResultVO sendCodeByLogin(String phone);

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return ResultVo
     */
    ResultVO login(String userAgent, LoginDTO loginDTO, Integer userType);

    /**
     * 获取登录令牌
     * @param loginKeyDTO 获取条件信息
     * @return ResultVO
     */
    ResultVO loginToken(LoginKeyDTO loginKeyDTO);


    /**
     * 用户登出
     * @param token token
     * @return ResultVo
     */
    ResultVO logOut(String token);

    /**
     * 获取用户信息
     * @return ResultVO
     */
    ResultVO me(String token);

    /**
     * 忘记密码：发送验证码
     * @param username  用户名
     * @param phone 手机号
     * @return ResultVO
     */
    ResultVO sendCodeByForgetPassword(String username, String phone);

    /**
     * 忘记密码：验证验证码
     * @param forgetPasswordDTO 忘记密码信息
     * @return ResultVO
     */
    ResultVO checkForgetPasswordCode(ForgetPasswordDTO forgetPasswordDTO);

    /**
     * 忘记密码：修改密码
     * @param forgetPasswordDTO 忘记密码信息
     * @return ResultVO
     */
    ResultVO updatePasswordByForgetPassword(ForgetPasswordDTO forgetPasswordDTO);

    /**
     * 修改用户名
     * @param username 新用户名
     * @return ResultVO
     */
    ResultVO updateUsername(String username);

    /**
     * 修改手机号：发送验证码
     * @param phone 手机号
     * @return ResultVO
     */
    ResultVO sendCodeByUpdatePhone(String phone);

    /**
     * 修改手机号
     * @param token 登录令牌
     * @param phone 手机号
     * @param code 验证码
     * @return ResultVO
     */
    ResultVO updatePhone(String token, String phone, String code);

    /**
     * 修改密码
     * @param token 登录令牌
     * @param updatePasswordDTO 修改密码信息
     * @return ResultVO
     */
    ResultVO updatePassword(String token, UpdatePasswordDTO updatePasswordDTO);


    /**
     * 修改用户头像
     * @param file 图片文件
     * @return ResultVO
     */
    ResultVO updateUserIcon(String token, MultipartFile file);

    /**
     * 用户：修改用户昵称
     * @param nickname 昵称
     * @return ResultVo
     */
    ResultVO updateNickName(String jwt, String nickname);

    /**
     * 签到
     * @return ResultVo
     */
    ResultVO signIn();

    /**
     * 查询签到记录
     * @return ResultVo
     */
    ResultVO querySignIn();

    /**
     * 获取本月连续签到天数
     * @return ResultVo
     */
    ResultVO queryContinuityDay();

    /*管理员相关接口*/

    /**
     * 查询用户（多条件）
     * @param page 页码
     * @param size 数量
     * @param userManagerDTO 查询条件
     * @return ResultVO
     */
    ResultVO findUser(Integer page, Integer size, UserManagerDTO userManagerDTO);

    /**
     * 查询系统用户（多条件）
     * @param page 页码
     * @param size 数量
     * @param userManagerDTO 查询条件
     * @return ResultVO
     */
    ResultVO findSystemUser(Integer page, Integer size, UserManagerDTO userManagerDTO);

    /**
     * 修改用户
     * @param updateUserDTO 修改用户信息
     * @param level 修改等级
     * @return ResultVO
     */
    ResultVO updateUser(UpdateUserDTO updateUserDTO, String level);

    /**
     * 新增用户
     * @param saveUserDTO 新增用户信息
     * @return ResultVO
     */
    ResultVO saveUser(SaveUserDTO saveUserDTO);

    /**
     * 上传或修改用户头像
     * @param file 图片文件
     * @return ResultVO
     */
    ResultVO uploadOrUpdateUserIcon(Long userId, MultipartFile file);

    /**
     * 强制下线
     * @param mainToken 主人登录令牌
     * @param coerceLogoutDTO 强制下线条件
     * @return ResultVO
     */
    ResultVO coerceLogout(String mainToken, CoerceLogoutDTO coerceLogoutDTO);

    /**
     * 停用用户
     * @param userId 用户Id
     * @param blockUpTime 停用时长（单位：分）
     * @return ResultVO
     */
    ResultVO blockUpUser(Long userId, Long blockUpTime);

    /**
     * 取消停用用户
     * @param userId 用户Id
     * @return ResultVO
     */
    ResultVO cancelBlockUp(Long userId);

    /**
     * 新注册用户统计
     * @param startDate 起始时间
     * @param endDate 截止时间
     * @return ResultVO
     */
    ResultVO registerUserCount(Long startDate, Long endDate);

    /**
     * 用户访问量统计
     * @param startDate 起始时间
     * @param endDate 截止时间
     * @return ResultVO
     */
    ResultVO userPVCount(Long startDate, Long endDate);

    /**
     * 在线用户统计
     * @return ResultVO
     */
    ResultVO loginUserCount();
}
