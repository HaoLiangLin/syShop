package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.authentication.SmsCodeAuthenticationToken;
import com.jy2b.zxxfd.domain.*;
import com.jy2b.zxxfd.domain.vo.BillStatusCode;
import com.jy2b.zxxfd.domain.vo.BillType;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.domain.vo.StatusCode;
import com.jy2b.zxxfd.mapper.*;
import com.jy2b.zxxfd.service.IUserService;
import com.jy2b.zxxfd.utils.*;
import com.jy2b.zxxfd.contants.RedisConstants;
import com.jy2b.zxxfd.contants.SystemConstants;
import com.jy2b.zxxfd.domain.dto.*;
import eu.bitwalker.useragentutils.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jy2b.zxxfd.contants.RedisConstants.*;
import static com.jy2b.zxxfd.contants.SystemConstants.*;
import static com.jy2b.zxxfd.domain.vo.StatusCode.INVALID_REQUEST;

/**
 * @author 林武泰
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserWallerMapper userWallerMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserBillUtils userBillUtils;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RedisUtils redisUtils;

    private final String savePath =  "/user/icon";

    @Override
    public ResultVO sendCodeByRegister(String phone) {
        // 发送验证码
        return sendCode(phone, REGISTER_CODE_KEY, REGISTER_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVO register(RegisterDTO registerDTO) {
        // 判断注册信息是否为空
        if (registerDTO == null) {
            return ResultVO.fail("注册信息不能为空");
        }
        // 获取用户名
        String username = registerDTO.getUsername();
        // 判断用户名格式
        if (RegexUtils.isUsernameInvalid(username)) {
            return ResultVO.fail("用户名格式错误，英文字母开头，包含大小写字母，数字和下划线");
        }
        // 获取手机号
        String phone = registerDTO.getPhone();
        // 判断手机是否为空
        if (StrUtil.isBlank(phone)) {
            return ResultVO.fail("手机号不能空");
        } else if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVO.fail("手机号格式错误");
        }
        // 获取密码
        String password = registerDTO.getPassword();
        // 判断密码是否为空
        if (StrUtil.isBlank(password)) {
            return ResultVO.fail("密码不能为空");
        }
        // 获取验证码
        String code = registerDTO.getCode();
        // 验证验证码
        String checkResult = checkCodes(phone, REGISTER_CODE_KEY, code);
        // 判断验证结果是否不为空
        if (StrUtil.isNotBlank(checkResult)) {
            return ResultVO.fail(checkResult);
        }

        // 查询用户名是否重复
        Integer usernameCount = query().eq("username", username).count();
        if (usernameCount > 0) {
            return ResultVO.fail("用户名已存在");
        }
        // 查询手机号是否重复
        Integer phoneCount = query().eq("phone", phone).count();
        if (phoneCount > 0) {
            return ResultVO.fail("手机号已存在");
        }

        // 注册用户
        userRegister(username, phone, password, null);

        return ResultVO.ok("用户注册成功");
    }

    @Override
    public ResultVO sendCodeByLogin(String phone) {
        // 验证手机号是否注册
        String checkResult = checkPhoneForLogin(phone);
        if (StrUtil.isNotBlank(checkResult)) {
            return ResultVO.fail(checkResult);
        }
        // 发送验证码
        return sendCode(phone, LOGIN_CODE_KEY, LOGIN_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVO login(String userAgent, LoginDTO loginDTO, Integer userType) {
        // 获取登录方式
        Integer loginType = loginDTO.getLoginType();
        if (loginType == null) {
            return ResultVO.fail("错误登录请求");
        }
        // 判断是否用户名密码登录
        if (loginType == 0) {
            // 获取用户名
            String username = loginDTO.getUsername();
            // 获取密码
            String password = loginDTO.getPassword();
            return usernamePasswordLogin(userAgent, username, password, userType);
        }
        // 判断是否手机号验证码登录
        if (loginType == 1) {
            // 获取手机号
            String phone = loginDTO.getPhone();
            // 获取验证码
            String code = loginDTO.getCode();
            return phoneCodeLogin(userAgent, phone, code);
        }
        return ResultVO.fail("错误登录请求");
    }

    @Override
    public ResultVO loginToken(LoginKeyDTO loginKeyDTO) {
        String username = loginKeyDTO.getUsername();
        String password = loginKeyDTO.getPassword();
        // 判断用户名是否为空
        if (StrUtil.isBlank(username)) {
            return ResultVO.fail("用户名不能为空");
        }
        // 判断是否无效用户名
        if (RegexUtils.isUsernameInvalid(username)) {
            return ResultVO.fail("用户名格式错误，英文字母开头，包含大小写字母，数字和下划线");
        }
        // 判断密码是否为空
        if (StrUtil.isBlank(password)) {
            return ResultVO.fail("密码不能为空");
        }

        // 查询管理员
        User admin = query().eq("username", username).eq("user_type", 0).one();
        // 判断管理员是否存在
        if (admin == null) {
            return new ResultVO(INVALID_REQUEST, null, "非法请求，禁止访问");
        }
        // 验证密码
        String encodePwd = admin.getPassword();
        if (!passwordEncoder.matches(password, encodePwd)) {
            return new ResultVO(INVALID_REQUEST, null, "非法请求，禁止访问");
        }
        // 判断管理员账号状态是否禁用
        if (admin.getStatus() == 1) {
            return ResultVO.fail("账号已被停用");
        }

        return ResultVO.ok(ADMIN_LOGIN_KEY, "登录令牌请求成功");
    }

    @Override
    public ResultVO logOut(String token) {
        // 获取用户信息key
        String userDTOKey = LOGIN_USER_KEY + token;
        // 删除Redis中的用户信息
        stringRedisTemplate.delete(userDTOKey);
        // 删除线程变量
        UserHolder.removeUser();

        // redis获取系统用户相关信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        // 获取用户权限信息
        String securityKey = LOGIN_KEY + userId;
        // 删除Redis中的权限信息
        stringRedisTemplate.delete(securityKey);

        // 删除登录注册
        stringRedisTemplate.delete(LOGIN_REGISTER_KEY + userId);

        return ResultVO.ok("退出登录成功");
    }

    @Override
    public ResultVO me(String token) {
        String cacheKey = LOGIN_USER_KEY + token;
        // 获取用户登录信息
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(cacheKey);

        // 将map转换成Bean
        UserDTO userDTO = BeanUtil.fillBeanWithMap(map, new UserDTO(), false);

        return ResultVO.ok(userDTO, "查询用户成功");
    }

    @Override
    public ResultVO sendCodeByForgetPassword(String username, String phone) {
        // 验证手机号是否注册
        String checkResult = checkPhoneForLogin(phone);
        if (StrUtil.isNotBlank(checkResult)) {
            return ResultVO.fail(checkResult);
        }
        // 验证用户是否绑定该手机号
        User user = query().eq("username", username).one();
        // 判断用户是否存在
        if (user == null) {
            return ResultVO.fail("用户名未注册");
        }
        // 判断用户是否绑定该手机号
        if (!phone.equals(user.getPhone())) {
            if ("无效手机号".equals(user.getPhone())) {
                return ResultVO.fail("当前用户名绑定手机号无效，请联系客服");
            }
            return ResultVO.fail("绑定手机号不一致");
        }
        // 发送验证码
        return sendCode(phone, username + FORGOT_PASSWORD_CODE_KEY, FORGOT_PASSWORD_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVO checkForgetPasswordCode(ForgetPasswordDTO forgetPasswordDTO) {
        // 获取手机号
        String phone = forgetPasswordDTO.getPhone();
        // 获取验证码
        String code = forgetPasswordDTO.getCode();
        // 获取用户名
        String username = forgetPasswordDTO.getUsername();

        // 验证验证码
        String checkResult = checkCode(phone, username + FORGOT_PASSWORD_CODE_KEY, code);
        if (StrUtil.isNotBlank(checkResult)) {
            return ResultVO.fail(checkResult);
        }
        return ResultVO.ok("验证通过");
    }

    @Override
    public ResultVO updatePasswordByForgetPassword(ForgetPasswordDTO forgetPasswordDTO) {
        // 获取手机号
        String phone = forgetPasswordDTO.getPhone();
        // 获取验证码
        String code = forgetPasswordDTO.getCode();
        // 获取用户名
        String username = forgetPasswordDTO.getUsername();
        // 验证验证码
        String checkResult = checkCodes(phone, username + FORGOT_PASSWORD_CODE_KEY, code);
        if (StrUtil.isNotBlank(checkResult)) {
            return ResultVO.fail(checkResult);
        }

        // 验证手机号是否注册
        String checkPhoneResult = checkPhoneForLogin(phone);
        if (StrUtil.isNotBlank(checkPhoneResult)) {
            return ResultVO.fail(checkResult);
        }

        // 获取新密码
        String newPassword = forgetPasswordDTO.getNewPassword();
        // 判断密码是否为空
        if (StrUtil.isBlank(newPassword)) {
            return ResultVO.fail("新密码不能为空");
        }
        // 密码加密
        String encodePassword = passwordEncoder.encode(newPassword);

        // 根据手机号查询用户
        User user = query().eq("phone", phone).one();

        // 获取禁用状态
        ResultVO resultVO = checkBlockUpUser(user.getId());
        if (resultVO != null) {
            return resultVO;
        }

        // 获取旧密码
        String oldPassword = user.getPassword();
        // 判断是否新密码是否与新密码一样
        if (passwordEncoder.matches(newPassword, oldPassword)) {
            return ResultVO.fail("新密码不能与旧密码一样");
        }
        // 修改用户密码
        user.setPassword(encodePassword);
        boolean updateResult = updateById(user);

        // 注销登录注册
        writeOffLoginRegister(user.getId());

        return updateResult ? ResultVO.ok("密码修改成功") : ResultVO.fail("密码修改失败");
    }

    @Override
    public ResultVO updateUsername(String username) {
        // 判断用户名是否为空
        if (StrUtil.isBlank(username)) {
            return ResultVO.fail("用户名不能为空");
        }
        // 判断用户名是否符合规范
        if (RegexUtils.isUsernameInvalid(username)) {
            return ResultVO.fail("用户名格式错误，英文字母开头，包含大小写字母，数字和下划线");
        }
        // 判断用户名是否已被使用
        Integer usernameCount = query().eq("username", username).count();
        if (usernameCount > 0) {
            return ResultVO.fail("用户名已被使用");
        }
        // 获取用户ID
        Long userId = UserHolder.getUser().getId();
        // 根据用户id查询用户
        User user = getById(userId);
        // 判断用户是否在一年内修改过用户名
        Date updateTime = user.getUsernameUpdateTime();

        if (updateTime == null) {
            //  修改用户名
            boolean update = update().set("username", username).set("username_update_time", TimeUtils.dateToStringTime(new Date())).eq("id", userId).update();
            return update ? ResultVO.ok("修改用户名成功") : ResultVO.fail("修改用户名失败");
        }

        // 获取日历对象
        Calendar updateCalendar = Calendar.getInstance();
        updateCalendar.setTime(updateTime);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());

        // 判断是否小于当前年
        if (updateCalendar.get(Calendar.YEAR) < nowCalendar.get(Calendar.YEAR)) {
            // 判断是否大于等于当前月
            if (updateCalendar.get(Calendar.MONTH) >= nowCalendar.get(Calendar.MONTH)) {
                // 判断是否大于大于当前天
                if (updateCalendar.get(Calendar.DAY_OF_MONTH) >= nowCalendar.get(Calendar.DAY_OF_MONTH)) {
                    //  修改用户名
                    boolean update = update().set("username", username).set("username_update_time", TimeUtils.dateToStringTime(new Date())).eq("id", userId).update();
                    return update ? ResultVO.ok("修改用户名成功") : ResultVO.fail("修改用户名失败");
                }
            }
        }

        return ResultVO.fail("用户名一年仅能修改一次");
    }

    @Override
    public ResultVO sendCodeByUpdatePhone(String phone) {
        // 判断是否为原手机号
        String nowPhone = UserHolder.getUser().getPhone();
        if (phone.equals(nowPhone)) {
            return ResultVO.fail("手机号为当前绑定手机号，无需更改");
        }
        // 发送验证码
        return sendCode(phone, UPDATE_PHONE_CODE_KEY, UPDATE_PHONE_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVO updatePhone(String token, String phone, String code) {
        // 验证验证码
        String checkCodes = checkCodes(phone, UPDATE_PHONE_CODE_KEY, code);
        if (StrUtil.isNotBlank(checkCodes)) {
            return ResultVO.fail(checkCodes);
        }
        // 判断手机号是否被注册
        User user = query().eq("phone", phone).one();
        if (user != null) {
            // 获取用户id
            Long userId = user.getId();
            // 将其手机号设置为无效手机号
            update().set("phone", "无效手机号").eq("id", userId).update();
        }
        // 获取当前用户id
        Long userId = UserHolder.getUser().getId();
        // 修改手机号
        boolean result = update().set("phone", phone).eq("id", userId).update();
        if (result) {
            // 修改Redis用户信息
            stringRedisTemplate.opsForHash().put(LOGIN_USER_KEY + token, "phone", phone);
            // 刷新token有效期
            stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
            // 修改Redis权限信息
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            loginUser.getUser().setPhone(phone);
            stringRedisTemplate.opsForValue()
                    .set(LOGIN_KEY + userId, JSONUtil.toJsonStr(loginUser), LOGIN_KEY_TTL, TimeUnit.MINUTES);
        }
        return result ? ResultVO.ok("手机号修改成功") : ResultVO.fail("手机号修改失败");
    }

    @Override
    public ResultVO updatePassword(String token,UpdatePasswordDTO updatePasswordDTO) {
        // 获取旧密码
        String oldPassword = updatePasswordDTO.getOldPassword();
        // 获取新密码
        String newPassword = updatePasswordDTO.getNewPassword();

        // 判断旧密码是否为空
        if (StrUtil.isBlank(oldPassword)) {
            return ResultVO.fail("旧密码不能为空");
        }
        // 判断新密码是否为空
        if (StrUtil.isBlank(newPassword)) {
            return ResultVO.fail("新密码不能为空");
        }

        // 获取用户原密码
        Long userId = UserHolder.getUser().getId();
        // 查询用户
        User user = getById(userId);
        // 获取用户原密码
        String nowPassword = user.getPassword();
        // 判断是否与原密码不一致
        if (!passwordEncoder.matches(oldPassword, nowPassword)) {
            return ResultVO.fail("旧密码错误");
        }

        // 判断新密码是否与旧密码一致
        if (oldPassword.equals(newPassword)) {
            return ResultVO.fail("新密码不能与旧密码一样");
        }

        // 新密码加密
        String encodePassword = passwordEncoder.encode(newPassword);
        // 修改密码
        boolean updateResult = update().set("password", encodePassword).eq("id", userId).update();
        // 判断是否修改成功
        if (updateResult) {
            // 退出登录
            logOut(token);
        }

        return updateResult ? ResultVO.ok("修改密码成功，请重新登录") : ResultVO.fail("修改密码失败");
    }

    @Override
    public ResultVO updateUserIcon(String token, MultipartFile file) {
        // 保存头像
        ResultVO resultVO = UploadUtils.saveFile(file, savePath);
        // 判断是否保存成功
        if (resultVO.getCode().equals(StatusCode.FAIL)) {
            return resultVO;
        }
        // 获取头像路径
        String fileNames = resultVO.getData().toString();
        // 获取用户ID
        Long userId = UserHolder.getUser().getId();

        // 修改用户头像
        boolean updateResult = update().set("icon", fileNames).eq("id", userId).update();
        // 判断是否修改成功
        if (updateResult) {
            // 获取用户旧头像
            String oldIcon = UserHolder.getUser().getIcon();
            if (StrUtil.isNotBlank(oldIcon)) {
                UploadUtils.deleteFile(oldIcon);
            }

            UserHolder.getUser().setIcon(fileNames);
            // 修改Redis用户信息
            stringRedisTemplate.opsForHash().put(LOGIN_USER_KEY + token, "icon", fileNames);
            // 刷新token有效期
            stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
            // 修改Redis权限信息
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            loginUser.getUser().setIcon(fileNames);
            stringRedisTemplate.opsForValue()
                    .set(LOGIN_KEY + userId, JSONUtil.toJsonStr(loginUser), LOGIN_KEY_TTL, TimeUnit.MINUTES);
        } else {
            UploadUtils.deleteFile(fileNames);
        }
        return updateResult ? ResultVO.ok(fileNames, "修改成功") : ResultVO.fail("修改失败");
    }

    @Override
    public ResultVO updateNickName(String jwt, String nickname) {
        if (StrUtil.isBlank(nickname)) {
            return ResultVO.fail("昵称不能为空");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        boolean result = update().set("nickname", nickname).eq("id", userId).update();
        if (result) {
            UserHolder.getUser().setNickname(nickname);
            // 修改Redis用户信息
            stringRedisTemplate.opsForHash().put(LOGIN_USER_KEY + jwt, "nickname", nickname);
            // 刷新token有效期
            stringRedisTemplate.expire(LOGIN_USER_KEY + jwt, LOGIN_USER_TTL, TimeUnit.MINUTES);
            // 修改Redis权限信息
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            loginUser.getUser().setNickname(nickname);
            stringRedisTemplate.opsForValue()
                    .set(LOGIN_KEY + userId, JSONUtil.toJsonStr(loginUser), LOGIN_KEY_TTL, TimeUnit.MINUTES);
        }
        return result ? ResultVO.ok(null,"昵称修改成功") : ResultVO.fail("昵称修改失败");
    }

    @Override
    public ResultVO signIn() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询用户是否已经签到
        boolean bit = getSignIn(userId);
        if (bit) {
            return ResultVO.ok(null,"今日已签到");
        }

        // 查询用户钱包
        UserWaller userWaller = userWallerMapper.selectById(userId);

        // 签到增加积分
        UpdateWrapper<UserWaller> userWallerUpdateWrapper = new UpdateWrapper<>();
        userWallerUpdateWrapper.set("points", userWaller.getPoints() + SystemConstants.SIGN_IN_POINTS).eq("id", userId);
        userWallerMapper.update(null, userWallerUpdateWrapper);

        // 新增账单
        userBillUtils.saveUserBill(userId, SystemConstants.SIGN_IN_POINTS, "积分","签到送积分", APPLICATION_NAME, BillType.income, null, BillStatusCode.rechargeSuccess, "");

        // 用户签到
        setSignIn(userId);

        return ResultVO.ok(null,"签到成功");
    }

    @Override
    public ResultVO querySignIn() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询用户是否已经签到
        boolean result = getSignIn(userId);
        return result ? ResultVO.ok(null,"今日已签到") : ResultVO.fail("今日未签到");
    }

    @Override
    public ResultVO queryContinuityDay() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 设置日期为key后缀
        String keySuffix = now.format(DateTimeFormatter.ofPattern("yyyy:MM"));
        // 拼接key
        String key = RedisConstants.SIGN_IN_KEY + userId + ":" + keySuffix;
        // 获取今天是第几天
        int dayOfMonth = now.getDayOfMonth();

        // 获取用户当月至今日的签到记录
        List<Long> result = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );

        if (result == null || result.isEmpty()) {
            return ResultVO.ok(0, "查询成功");
        }

        Long number = result.get(0);

        if (number == null || number == 0) {
            return ResultVO.ok(0, "查询成功");
        }

        // 签到天数
        int count = 0;
        while (true) {
            // 将数字与1作与运算，得到最后一个bit位
            if ((number & 1) == 0) {
                // 未签到
                break;
            } else {
                // 有签到
                count ++;
            }
            // 将数字右移一位
            number >>>= 1;
        }

        return ResultVO.ok(count, "查询成功");
    }

    @Override
    public ResultVO findUser(Integer page, Integer size, UserManagerDTO userManagerDTO) {
        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> userQueryWrapper = selectUser(userManagerDTO, "user");
        userMapper.selectPage(userPage, userQueryWrapper);
        return ResultVO.ok(userPage, "查询用户成功");
    }

    @Override
    public ResultVO findSystemUser(Integer page, Integer size, UserManagerDTO userManagerDTO) {
        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> userQueryWrapper = selectUser(userManagerDTO, "admin");
        userMapper.selectPage(userPage, userQueryWrapper);
        return ResultVO.ok(userPage, "查询用户成功");
    }

    @Override
    public ResultVO updateUser(UpdateUserDTO updateUserDTO, String level) {
        // 获取用户id
        Long userID = updateUserDTO.getId();
        // 判断用户id是否为空
        if (userID == null) {
            return ResultVO.fail("用户id不能为空");
        }
        // 查询修改用户是否存在
        User user = getById(userID);
        if (user == null) {
            return ResultVO.fail("用户不存在");
        }
        // 获取用户类型
        Integer type = user.getUserType();
        // 判断修改等级是否为用户
        if ("user".equals(level)) {
            // 判断是否管理员类型
            if (type.equals(0)) {
                return ResultVO.fail("无法修改");
            }
        }

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        // 获取用户名
        String username = updateUserDTO.getUsername();
        // 判断用户名是否不为空
        if (StrUtil.isNotBlank(username)) {
            // 判断用户名是否符合规范
            if (RegexUtils.isUsernameInvalid(username)) {
                return ResultVO.fail("用户名格式错误，英文字母开头，包含大小写字母，数字和下划线");
            }
            // 判断用户名是否存在
            Integer usernameCount = query().eq("username", username).count();
            if (usernameCount > 0) {
                return ResultVO.fail("用户名已存在");
            }
            // 添加用户名条件
            userUpdateWrapper.set("username", username);
        }

        // 获取手机号
        String phone = updateUserDTO.getPhone();
        // 判断手机号是否不为空
        if (StrUtil.isNotBlank(phone)) {
            // 判断手机号是否格式正确
            if (RegexUtils.isPhoneInvalid(phone)) {
                return ResultVO.fail("手机号格式错误");
            }
            // 判断手机号是否存在
            Integer phoneCount = query().eq("phone", phone).count();
            if (phoneCount > 0) {
                return ResultVO.fail("手机号已存在");
            }
            // 添加手机号条件
            userUpdateWrapper.set("phone", phone);
        }

        // 获取用户昵称
        String nickname = updateUserDTO.getNickname();
        // 判断是否不为空
        if (StrUtil.isNotBlank(nickname)) {
            if ("admin".equals(level)) {
                // 添加用户状态条件
                userUpdateWrapper.set("nickname", nickname);
            }
        }

        // 获取用户类型
        Integer userType = updateUserDTO.getUserType();
        // 判断是否不为空
        if (userType != null) {
            if ("admin".equals(level)) {
                // 添加用户类型条件
                if (userType == 0 || userType == 1) {
                    userUpdateWrapper.set("user_type", userType);
                }
            }
        }

        // 获取用户状态
        Integer status = updateUserDTO.getStatus();
        // 判断用户状态是否不为空
        if (status != null) {
            // 判断修改等级是否为管理员
            if ("admin".equals(level)) {
                // 获取用户id
                Long adminID = UserHolder.getUser().getId();
                // 判断是否修改自身
                if (userID.equals(adminID) && status == 1) {
                    return ResultVO.fail("无法停用自己");
                }
            }
            // 添加用户状态条件
            if (status == 0 || status == 1) {
                userUpdateWrapper.set("status", status);
            }
        }
        // 添加用户id条件
        userUpdateWrapper.eq("id", userID);

        // 修改用户
        boolean updateResult = update(userUpdateWrapper);
        return updateResult ? ResultVO.ok("修改用户成功") : ResultVO.fail("修改用户失败");
    }

    @Override
    public ResultVO saveUser(SaveUserDTO saveUserDTO) {
        // 获取用户名
        String username = saveUserDTO.getUsername();
        // 判断用户名是否为空
        if (StrUtil.isBlank(username)) {
            return ResultVO.fail("用户名不能为空");
        }
        // 获取手机号
        String phone = saveUserDTO.getPhone();
        // 判断手机号是否为空
        if (StrUtil.isBlank(phone)) {
            return ResultVO.fail("手机号不能为空");
        }
        // 判断手机号格式是否正确
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVO.fail("手机号格式错误");
        }
        // 获取密码
        String password = saveUserDTO.getPassword();
        // 判断密码是否为空
        if (StrUtil.isBlank(password)) {
            return ResultVO.fail("密码不能为空");
        }
        // 获取用户类型
        Integer userType = saveUserDTO.getUserType();
        // 判断是否为空
        if (userType == null) {
            userType = 1; // 默认为用户
        }
        // 判断是否有效值
        if (userType < 0 || userType > 1) {
            userType = 1; // 默认为用户
        }
        // 获取用户状态
        Integer status = saveUserDTO.getStatus();
        // 判断状态是否为空
        if (status == null) {
            status = 1; // 默认停用
        }
        // 判断状态是否有效值
        if (status < 0 || status > 1) {
            status = 1; // 默认停用
        }
        // 判断用户名是否存在
        Integer usernameCount = query().eq("username", username).count();
        if (usernameCount > 0) {
            return ResultVO.fail("用户名已存在");
        }
        // 判断手机号是否存在
        Integer phoneCount = query().eq("phone", phone).count();
        if (phoneCount > 0) {
            return ResultVO.fail("手机号已存在");
        }

        // 判断用户类型是否为用户
        if (userType == 1) {
            User user = userRegister(username, phone, password, status);
            return ResultVO.ok(user, "新增用户成功");
        }

        // 保存新用户
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(username);
        user.setUserType(userType);
        user.setStatus(status);

        boolean saveResult = save(user);

        return saveResult ? ResultVO.ok(user, "新增管理员成功，暂未设置角色") : ResultVO.fail("新增用户失败");
    }

    @Override
    public ResultVO uploadOrUpdateUserIcon(Long userId, MultipartFile file) {
        // 查询用户
        User user = getById(userId);
        // 判断用户是否存在
        if (user == null) {
            return ResultVO.fail("用户不存在");
        }
        // 获取用户头像
        String icon = user.getIcon();
        // 判断是否删除头像
        if (file == null || file.isEmpty()) {
            // 判断是否设置头像
            if (StrUtil.isBlank(icon)) {
                return ResultVO.fail("头像不存在");
            }
            // 删除用户头像
            boolean updateResult = update().set("icon", null).eq("id", userId).update();
            if (updateResult) {
                UploadUtils.deleteFile(icon);
                return ResultVO.ok("删除头像成功");
            }
            return ResultVO.fail("删除头像失败");
        }

        // 保存新头像
        ResultVO resultVO = UploadUtils.saveFile(file, savePath);
        // 判断是否保存成功
        if (resultVO.getCode().equals(StatusCode.FAIL)) {
            return resultVO;
        }
        // 获取头像路径
        String fileNames = resultVO.getData().toString();
        // 修改用户头像
        boolean updateResult = update().set("icon", fileNames).eq("id", userId).update();
        // 判断是否修改成功
        if (updateResult) {
            // 删除旧头像
            if (StrUtil.isNotBlank(icon)) {
                UploadUtils.deleteFile(icon);
            }
        } else {
            // 删除新头像
            UploadUtils.deleteFile(fileNames);
        }
        return updateResult ? ResultVO.ok(fileNames, "修改成功") : ResultVO.fail("修改失败");
    }

    @Override
    public ResultVO coerceLogout(String mainToken, CoerceLogoutDTO coerceLogoutDTO) {
        // 获取用户ID
        Long userId = coerceLogoutDTO.getUserId();
        // 判断用户ID是否为空
        if (userId == null) {
            return ResultVO.fail("用户ID不能为空");
        }
        // 获取用户token
        String token = coerceLogoutDTO.getToken();
        // 判断用户登录令牌是否为空
        if (StrUtil.isBlank(token)) {
            return ResultVO.fail("用户登录令牌不能为空");
        }

        // 判断是否下线自己
        if (mainToken.equals(token)) {
            return ResultVO.fail("无法强制下线自己");
        }

        // 获取用户信息key
        String userDTOKey = LOGIN_USER_KEY + token;
        // 删除Redis中的用户信息
        stringRedisTemplate.delete(userDTOKey);

        // 删除登录注册
        stringRedisTemplate.delete(LOGIN_REGISTER_KEY + userId);
        return ResultVO.ok("已强制下线用户:" + userId);
    }

    @Override
    public ResultVO blockUpUser(Long userId, Long blockUpTime) {
        // 根据ID查询用户
        User user = getById(userId);
        // 判断是否最高管理员
        if (userId == 1) {
            return ResultVO.fail("无法禁用");
        }
        // 判断用户是否存在
        if (user == null) {
            return ResultVO.fail("用户不存在");
        }

        if (blockUpTime == null) {
            blockUpTime = 1L;
        }

        // 注销登录注册
        writeOffLoginRegister(userId);

        // 获取禁用key
        String key = BLOCK_UP_USER_KEY + userId;
        // 禁用用户
        stringRedisTemplate.opsForValue().set(key, "该账号已被禁用", blockUpTime, TimeUnit.MINUTES);

        return ResultVO.ok("用户:" + userId + "已被禁用！禁用时长:" + blockUpTime + "分钟");
    }

    @Override
    public ResultVO cancelBlockUp(Long userId) {
        // 获取禁用key
        String key = BLOCK_UP_USER_KEY + userId;
        // 删除禁用用户
        Boolean delete = stringRedisTemplate.delete(key);
        return ResultVO.ok("用户:" + userId + "已取消禁用");
    }

    @Override
    public ResultVO registerUserCount(Long startDate, Long endDate) {
        // 获取起始时间
        Date start = new Date(startDate);
        // 获取截止时间
        Date end = new Date(endDate);
        // 判断截止时间是否早于起始时间
        if (!start.equals(end)) {
            if (start.after(end)) {
                return ResultVO.fail("截止时间不得早于起始时间");
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateTime = simpleDateFormat.format(start);
        String endDateTime = simpleDateFormat.format(end);

        // 查询注册时间在指定区间内的用户
        List<User> userList = query().ge("create_time", startDateTime).le("create_time", endDateTime).orderByAsc("create_time").list();

        // 获取指定时间段中全部日期
        List<Date> dumDateList = TimeUtils.getDumDateList(startDate, endDate);

        List<Map<String, Object>> resultMap = new ArrayList<>();
        for (Date date : dumDateList) {
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");

            Map<String, Object> listMap = new HashMap<>();
            String nowDate = simpleDate.format(date);
            List<User> filterResult = userList.stream().filter(user -> {
                simpleDate.setTimeZone(TimeZone.getTimeZone("GTM+8"));
                String createTime = simpleDate.format(user.getCreateTime());
                return createTime.equals(nowDate);
            }).collect(Collectors.toList());

            listMap.put("time", nowDate);
            listMap.put("data", filterResult);

            resultMap.add(listMap);
        }
        Map<String, Object> listMap = new HashMap<>();
        listMap.put("data", resultMap);
        listMap.put("total", userList.size());

        return ResultVO.ok(listMap, "查询成功");
    }

    @Override
    public ResultVO userPVCount(Long startDate, Long endDate) {
        // 获取起始时间
        Date start = new Date(startDate);
        // 获取截止时间
        Date end = new Date(endDate);
        // 判断截止时间是否早于起始时间
        if (!start.equals(end)) {
            if (start.after(end)) {
                return ResultVO.fail("截止时间不得早于起始时间");
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

        // 获取指定时间段中全部日期
        List<Date> dumDateList = TimeUtils.getDumDateList(startDate, endDate);
        // 总访问人数
        long count = 0;
        List<Map<String, Object>> resultMap = new ArrayList<>();
        for (Date date : dumDateList) {
            Map<String, Object> listMap = new HashMap<>();
            String format = simpleDateFormat.format(date);
            // 获取当日访问总量
            Long size = stringRedisTemplate.opsForHyperLogLog().size(USER_PV_KEY + format);
            count += size;

            listMap.put("time", simpleDateFormat1.format(date));
            listMap.put("quantity", size);
            resultMap.add(listMap);
        }
        Map<String, Object> listMap = new HashMap<>();
        listMap.put("data", resultMap);
        listMap.put("total", count);

        return ResultVO.ok(listMap, "查询成功");
    }

    @Override
    public ResultVO loginUserCount() {
        Set<String> keys = stringRedisTemplate.keys(LOGIN_REGISTER_KEY + "*");
        // 定义在线人数
        int onlineNumber = 0;
        // 定义在线人数名单
        List<User> onlineUserList = new ArrayList<>();
        if (keys != null && !keys.isEmpty()) {
            // 获取登录人数
            onlineNumber = keys.size();
            // 循环遍历登录人数
            for (String key : keys) {
                String[] split = key.split(":");
                String userId = split[1];
                User user = getById(Long.valueOf(userId));
                onlineUserList.add(user);
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("onlineNumber", onlineNumber);
        resultMap.put("onlineUsers", onlineUserList);
        return ResultVO.ok(resultMap, "查询成功");
    }

    /**
     * 获取用户当日是否签到
     * @param userId 用户id
     * @return boolean
     */
    private boolean getSignIn(Long userId) {
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 设置日期为key后缀
        String keySuffix = now.format(DateTimeFormatter.ofPattern("yyyy:MM"));
        // 拼接key
        String key = RedisConstants.SIGN_IN_KEY + userId + ":" + keySuffix;

        // 获取今天是第几天
        int dayOfMonth = now.getDayOfMonth();

        // 查询用户是否已经签到
        Boolean bit = stringRedisTemplate.opsForValue().getBit(key, dayOfMonth - 1);

        return Boolean.TRUE.equals(bit);
    }

    /**
     * 用户签到
     * @param userId 用户id
     */
    private void setSignIn(Long userId) {
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 设置日期为key后缀
        String keySuffix = now.format(DateTimeFormatter.ofPattern("yyyy:MM"));
        // 拼接key
        String key = RedisConstants.SIGN_IN_KEY + userId + ":" + keySuffix;
        // 获取今天是第几天
        int dayOfMonth = now.getDayOfMonth();

        // 用户签到，存入redis
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
    }

    /**
     * 生成查询用户条件构造器
     * @param userManagerDTO 查询用户条件信息
     * @param level 查询等级，admin：包含管理员，user：仅有用户
     * @return QueryWrapper<User>
     */
    private QueryWrapper<User> selectUser(UserManagerDTO userManagerDTO, String level) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();

        // 判断条件是否为空
        if (userManagerDTO == null) {
            if ("admin".equals(level)) {
                // 返回全部系统用户
                return userQueryWrapper;
            }
            // 返回全部用户
            return userQueryWrapper.eq("user_type", 1);
        }

        // 获取用户id
        Long userID = userManagerDTO.getId();
        // 判断用户id是否不为空
        if (userID != null) {
            // 添加id条件
            userQueryWrapper.eq("id", userID);
        }

        // 获取用户名
        String username = userManagerDTO.getUsername();
        // 判断用户名是否不为空
        if (StrUtil.isNotBlank(username)) {
            // 添加用户名条件
            userQueryWrapper.like("username", username);
        }

        // 获取手机号
        String phone = userManagerDTO.getPhone();
        // 判断手机号是否不为空
        if (StrUtil.isNotBlank(phone)) {
            // 添加手机号条件
            userQueryWrapper.like("phone", phone);
        }

        // 获取用户类型
        Integer type = userManagerDTO.getUserType();
        // 判断搜索等级是否用户
        if ("user".equals(level)) {
            // 默认用户类型为用户
            type = 1;
        }
        // 判断用户类型是否不为空
        if (type != null) {
            // 添加用户类型条件
            userQueryWrapper.eq("user_type", type);
        }

        // 获取账号状态
        Integer status = userManagerDTO.getStatus();
        // 判断用户状态是否不为空
        if (status != null && (status == 0 || status == 1)) {
            // 添加用户状态条件
            userQueryWrapper.eq("status", status);
        }

        // 获取注册时间排序
        Integer createTimeSort = userManagerDTO.getCreateTimeSort();
        // 判断注册时间排序是否不为空
        if (createTimeSort != null) {
            // 添加注册时间排序条件
            if (createTimeSort.equals(DESCENDING_SORT)) {
                userQueryWrapper.orderByDesc("create_time");
            }
            if (createTimeSort.equals(ASCENDING_SORT)) {
                userQueryWrapper.orderByAsc("create_time");
            }
        }

        // 获取最后登录时间排序
        Integer lastLoginSort = userManagerDTO.getLastLoginSort();
        // 判断最后登录时间排序是否不为空
        if (lastLoginSort != null) {
            // 添加注册时间排序条件
            if (lastLoginSort.equals(DESCENDING_SORT)) {
                userQueryWrapper.orderByDesc("last_login");
            }
            if (lastLoginSort.equals(ASCENDING_SORT)) {
                userQueryWrapper.orderByAsc("last_login");
            }
        }

        // 根据条件查询用户
        return userQueryWrapper;
    }
    /**
     * 账号密码登录
     * @param username 用户名
     * @param password 密码
     * @param userType 用户类型
     * @return ResultVo
     */
    private ResultVO usernamePasswordLogin(String userAgent, String username, String password, Integer userType) {
        // 判断用户名是否为空
        if (StrUtil.isBlank(username)) {
            return ResultVO.fail("用户名不能为空");
        }
        // 判断是否无效用户名
        if (RegexUtils.isUsernameInvalid(username)) {
            return ResultVO.fail("用户名格式错误，英文字母开头，包含大小写字母，数字和下划线");
        }
        // 判断密码是否为空
        if (StrUtil.isBlank(password)) {
            return ResultVO.fail("密码不能为空");
        }
        // 交由spring security进行验证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return ResultVO.fail("用户名或密码错误");
        }
        // 获取用户信息
        User user = loginUser.getUser();
        // 判断是否管理员登录
        if (userType != null) {
            // 判断是否为管理员身份
            if (user.getUserType() != 0) {
                return ResultVO.fail("用户名或密码错误");
            }
            // 判断登录令牌是否正确
            if (!userType.equals(ADMIN_LOGIN_KEY)) {
                return ResultVO.fail("登录令牌错误");
            }
        } else {
            // 判断是否为用户身份
            if (user.getUserType() != 1) {
                return ResultVO.fail("用户名或密码错误");
            }
        }

        // 登录验证
        boolean checkLogin = checkLogin(user.getId());
        if (!checkLogin) {
            // 注销登录注册
            writeOffLoginRegister(user.getId());
        }

        // 获取禁用状态
        ResultVO resultVO = checkBlockUpUser(user.getId());
        if (resultVO != null) {
            return resultVO;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        // 修改用户最后登录时间
        update().set("last_login", time).eq("id", user.getId()).update();

        // 存在，将用户信息DTO写入Redis
        String token = saveUserToDTO(user.getId());

        // 登录注册
        loginRegister(user.getId(), userAgent, token, time);

        // 将系统用户相关信息存入Redis
        redisUtils.setStr(LOGIN_KEY + user.getId(), loginUser, LOGIN_KEY_TTL, TimeUnit.MINUTES);

        // 返回token
        return ResultVO.ok(token, "登录成功");
    }
    /**
     * 手机验证码登录
     * @param phone 手机号
     * @param code 验证码
     * @return ResultVo
     */
    private ResultVO phoneCodeLogin(String userAgent, String phone, String code) {
        // 判断手机号是否为空
        if (StrUtil.isBlank(phone)) {
            return ResultVO.fail("手机号不能为空");
        }
        // 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVO.fail("手机号格式错误");
        }
        // 判断验证码是否为空
        if (StrUtil.isBlank(code)) {
            return ResultVO.fail("验证码不能为空");
        }

        // 交由将请求信息进行封装成token，并返回
        SmsCodeAuthenticationToken authenticationToken = new SmsCodeAuthenticationToken(phone, code);
        // 进行认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return ResultVO.fail("手机号或验证码错误");
        }
        // 获取用户id
        Long userId = loginUser.getUser().getId();

        // 登录验证
        boolean checkLogin = checkLogin(userId);
        if (!checkLogin) {
            // 注销登录注册
            writeOffLoginRegister(userId);
        }

        // 获取禁用状态
        ResultVO resultVO = checkBlockUpUser(userId);
        if (resultVO != null) {
            return resultVO;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        // 修改用户最后登录时间
        update().set("last_login", time).eq("id", userId).update();

        // 将用户信息DTO写入Redis
        String token = saveUserToDTO(userId);

        // 登录注册
        loginRegister(userId, userAgent, token, time);

        // 将系统用户相关信息存入Redis
        redisUtils.setStr(LOGIN_KEY + userId, loginUser, LOGIN_KEY_TTL, TimeUnit.MINUTES);

        // 返回token
        return ResultVO.ok(token, "登录成功");
    }

    /**
     * 禁用用户验证
     * @param userId 用户ID
     * @return 返回null则非禁用
     */
    private ResultVO checkBlockUpUser(Long userId) {
        // 获取禁用状态
        String blockUpMsg = stringRedisTemplate.opsForValue().get(BLOCK_UP_USER_KEY + userId);
        if (StrUtil.isNotBlank(blockUpMsg)) {
            Long expire = stringRedisTemplate.getExpire(BLOCK_UP_USER_KEY + userId, TimeUnit.MINUTES);

            Map<String, Object> blockUpMap = new HashMap<>();
            blockUpMap.put("message", blockUpMsg);
            blockUpMap.put("expire", expire);
            blockUpMap.put("timeUnit", "分钟");

            return ResultVO.ok(blockUpMap, "登录失败");
        }

        return null;
    }

    /**
     * 登录注册 防止token盗用
     * @param userId 用户ID
     * @param userAgent userAgent请求头
     * @param jwt 登录令牌
     * @param loginTime 登录时间
     */
    private void loginRegister(Long userId, String userAgent, String jwt, String loginTime) {
        //解析agent字符串
        UserAgent agent = UserAgent.parseUserAgentString(userAgent);
        //获取浏览器对象
        Browser browser = agent.getBrowser();
        //获取操作系统对象
        OperatingSystem operatingSystem = agent.getOperatingSystem();

        String systemName = operatingSystem.getName();// 操作系统
        String browserName = browser.getName(); // 登录浏览器
        BrowserType browserType = browser.getBrowserType(); // 登录浏览器类型
        Version browserVersion = agent.getBrowserVersion();// 登录浏览器版本

        // 获取Key
        String key = LOGIN_REGISTER_KEY + userId;
        // 设置值
        Map<String, String> map = new HashMap<>();
        map.put("time", loginTime); // 设置登录时间
        map.put("token", jwt); // 设置登录token
        map.put("systemName", systemName); // 设置操作系统名
        map.put("browserName", browserName); // 设置登录浏览器
        map.put("browserType", browserType.getName()); // 设置登录浏览器类型
        if (browserVersion != null && browserVersion.getVersion() != null) {
            map.put("browserVersion", browserVersion.getVersion()); // 设置登录浏览器版本
        }

        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, LOGIN_REGISTER_TTL, TimeUnit.MINUTES); // 设置定时
    }

    /**
     * 登录验证
     * @param userId 用户ID
     * @return boolean 通过即可以正常登录，false则已被登录，可令其强制下线
     */
    private boolean checkLogin(Long userId) {
        // 获取Key
        String key = LOGIN_REGISTER_KEY + userId;
        // 查询登录登记
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        // 判断是否为空
        if (map.isEmpty()) {
            return true;
        }
        // 获取token
        Object token = map.get("token");
        // 判断是否为空
        if (token == null) {
            return true;
        }

        return false;
    }

    /**
     * 注销登录登记
     * @param userId 用户ID
     */
    private void writeOffLoginRegister(Long userId) {
        // 获取Key
        String key = LOGIN_REGISTER_KEY + userId;
        // 查询登录登记
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        // 判断是否为空
        if (map.isEmpty()) {
            return;
        }
        // 注销登录登记
        // 获取token
        String token = map.get("token").toString();
        // 判断是否为空
        if (token != null) {
            // 强制下线
            // 获取用户信息key
            String userDTOKey = LOGIN_USER_KEY + token;
            // 删除Redis中的用户信息
            stringRedisTemplate.delete(userDTOKey);
        }
        // 删除登录登记
        stringRedisTemplate.delete(key);
    }

    /**
     * 保存用户并返回token
     * @param userId 保存的用户id
     */
    private String saveUserToDTO(Long userId) {
        // 生成token
        String jwt = JwtUtils.generateJwt(userId);
        // 设置Redis的key
        String key = LOGIN_USER_KEY + jwt;

        User user = getById(userId);
        // 将user拷贝成UserDTO
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        // 将Bean转为Map类型，并存入Redis
        Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        // 自定义将值转为字符串
                        // .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString())
                        .setFieldValueEditor((fieldName, fieldValue) -> {
                            if (fieldValue == null) {
                                fieldValue = "";
                            } else {
                                fieldValue += "";
                            }
                            return fieldValue;
                        })
        );
        // 将用户信息存入Redis
        stringRedisTemplate.opsForHash().putAll(key, map);
        // 设置到期期限 60分钟
        stringRedisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        return jwt;
    }
    /**
     * 用户注册
     * @param username 用户名
     * @param phone 手机号
     * @param password 密码
     */
    private User userRegister(String username, String phone, String password, Integer status) {
        // 密码加密
        String encodePassword = passwordEncoder.encode(password);
        // 新增用户
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(encodePassword);
        if (status != null) {
            user.setStatus(status);
        }
        user.setNickname(USER_NICK_NAME_PREFIX + phone);
        boolean registerResult = save(user);
        // 判断是否新增成功
        if (!registerResult) {
            throw new RuntimeException("用户注册失败");
        }
        // 获取用户id
        Long userId = user.getId();

        // 为用户设置角色
        QueryWrapper<Role> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("name", "普通用户");
        // 获取角色ID
        Long roleId = roleMapper.selectOne(userRoleQueryWrapper).getId();
        // 设置用户角色
        int userRoleResult = userRoleMapper.insert(new UserRole(userId, roleId));
        if (userRoleResult < 1) {
            throw new RuntimeException("用户注册失败");
        }
        // 新建用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        int userInfoResult = userInfoMapper.insert(userInfo);
        if (userInfoResult < 1) {
            throw new RuntimeException("用户注册失败");
        }
        // 新增用户钱包
        UserWaller userWaller = new UserWaller();
        userWaller.setId(userId);
        userWaller.setBalance(0.00);
        userWaller.setPoints(100L);
        userWaller.setRecharge(0.00);
        userWaller.setSpending(0.00);
        int userWallerResult = userWallerMapper.insert(userWaller);
        if (userWallerResult < 1) {
            throw new RuntimeException("用户注册失败");
        }

        return user;
    }

    /**
     * 验证手机号是否注册
     * @param phone 手机号
     * @return 验证通过则返回null
     */
    private String checkPhoneForLogin(String phone) {
        // 判断手机号是否为空
        if (StrUtil.isBlank(phone)) {
            return "手机号不能为空";
        }
        // 判断手机号格式是否无效
        if (RegexUtils.isPhoneInvalid(phone)) {
            return "手机号格式错误";
        }
        // 根据手机号查询用户
        User user = query().eq("phone", phone).one();
        // 判断手机号是否被注册
        if (user == null) {
            return "手机号未注册";
        }
        // 判断用户状态
        if (user.getStatus() == 1) {
            return "账号已被停用";
        }
        return null;
    }
    /**
     * 发送验证码
     * @param phone 手机号
     * @param type 验证码类型
     * @param time 时间
     * @param timeUnit 时间单位
     * @return ResultVO
     */
    private ResultVO sendCode(String phone, String type, Long time, TimeUnit timeUnit) {
        // 判断手机号是否为空
        if (StrUtil.isBlank(phone)) {
            return ResultVO.fail("手机号不能为空");
        }
        // 生成验证码
        String code = createCode(phone, type, time, timeUnit);
        // 判断是否生成失败
        if (StrUtil.isBlank(code)) {
            return ResultVO.fail("手机号格式错误");
        }

        CommunicationUtils.sendCode(phone, code, Long.toString(time));
        // 返回验证码
        return ResultVO.ok(null, "验证码已发送");
    }
    /**
     * 生成验证码
     * @param phone 手机号
     * @param type 验证码类型
     * @param time 验证码有效时间
     * @param timeUnit 时间单位
     * @return String
     */
    private String createCode(String phone, String type, Long time, TimeUnit timeUnit) {
        // 判断手机号是否为空
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        // 判断手机号格式是否无效
        if (RegexUtils.isPhoneInvalid(phone)) {
            return null;
        }

        // 随机生成六位数 验证码
        String code = RandomUtil.randomNumbers(6);

        // 拼接key
        String key = type + phone;
        // 将验证码存入Redis
        stringRedisTemplate.opsForValue().set(key, code, time, timeUnit);

        // 返回验证码
        return code;
    }
    /**
     * 验证验证码，无返回则为正确，并删除验证通过的验证码
     * @param phone 手机号
     * @param type 验证类型
     * @param code 验证码
     * @return String
     */
    private String checkCodes(String phone, String type, String code) {
        String checkResult = checkCode(phone, type, code);
        if (StrUtil.isNotBlank(checkResult)) {
            return checkResult;
        }
        // 拼接key
        String key = type + phone;
        // 验证码通过，删除验证码
        redisUtils.del(key);
        return null;
    }
    /**
     * 验证验证码，无返回则为正确
     * @param phone 手机号
     * @param type 验证类型
     * @param code 验证码
     * @return String
     */
    private String checkCode(String phone, String type, String code) {
        // 判断手机号是否为空
        if (StrUtil.isBlank(phone)) {
            return "手机号不能为空";
        }
        // 判断手机号格式是否无效
        if (RegexUtils.isPhoneInvalid(phone)) {
            return "手机号格式错误";
        }
        // 判断验证码是否为空
        if (StrUtil.isBlank(code)) {
            return "验证码不能为空";
        }

        // 拼接key
        String key = type + phone;
        // 获取Redis验证码
        String redisCode = stringRedisTemplate.opsForValue().get(key);

        // 判断Redis验证码是否不存在
        if (StrUtil.isBlank(redisCode)) {
            return "验证码无效";
        }
        // 验证码比较
        if (!code.equals(redisCode)) {
            return "验证码错误";
        }
        return null;
    }
}
