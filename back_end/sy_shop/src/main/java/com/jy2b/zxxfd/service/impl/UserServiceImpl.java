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
import com.jy2b.zxxfd.mapper.UserMapper;
import com.jy2b.zxxfd.mapper.UserRoleMapper;
import com.jy2b.zxxfd.service.IAccountService;
import com.jy2b.zxxfd.service.IUserInfoService;
import com.jy2b.zxxfd.service.IUserService;
import com.jy2b.zxxfd.utils.*;
import com.jy2b.zxxfd.contants.RedisConstants;
import com.jy2b.zxxfd.contants.SystemConstants;
import com.jy2b.zxxfd.domain.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.jy2b.zxxfd.contants.RedisConstants.UPDATE_PHONE_CODE_KEY;
import static com.jy2b.zxxfd.contants.RedisConstants.UPDATE_PHONE_CODE_TTL;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;

    @Resource
    private IUserInfoService userInfoService;

    @Resource
    private IAccountService accountService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public ResultVo registerCode(String phone) {
        // 1. 判断手机号是否被注册
        Integer count = query().eq("phone", phone).count();
        if (count > 0) {
            return ResultVo.fail("该手机号已注册");
        }

        // 2. 生成验证码
        return createCode(phone, RedisConstants.REGISTER_CODE_KEY, RedisConstants.REGISTER_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVo register(RegisterFromDTO register) {
        // 1. 获取手机号
        String phone = register.getPhone();
        // 2. 获取用户名
        String username = register.getUsername();

        // 3. 判断手机号是否被注册
        Integer phoneCount = query().eq("phone", phone).count();
        if (phoneCount > 0) {
            return ResultVo.fail("该手机号已注册");
        }
        // 4. 判断账号是否存在
        Integer usernameCount = query().eq("username", register.getUsername()).count();
        if (usernameCount > 0) {
            return ResultVo.fail("该账号已存在");
        }

        // 5. 判断是否无效账号
        if (RegexUtils.isUsernameInvalid(username)) {
            return ResultVo.fail("用户名格式错误！必须以字母开头，仅可包含字母、数字和下划线");
        }

        // 6. 获取密码
        String password = register.getPassword();

        // 7. 判断密码是否为空
        if (StrUtil.isBlank(password)) {
            return ResultVo.fail("密码不能为空");
        }

        // 8. 获取验证码
        String code = register.getCode();
        // 9. 验证验证码
        ResultVo resultVo = checkCode(RedisConstants.REGISTER_CODE_KEY, phone, code);
        if (!resultVo.getSuccess()) {
            return resultVo;
        }

        // 10. 创建用户对象
        User user = new User();
        // 设置用户名
        user.setUsername(username);
        // 设置手机号
        user.setPhone(phone);
        // 设置密码
        user.setPassword(passwordEncoder.encode(password));
        // 设置默认用户昵称
        String nickname = SystemConstants.USER_NICK_NAME_PREFIX + user.getPhone();
        user.setNickname(nickname);
        // 设置用户类型
        user.setUserType(1);

        // 11. 注册账号
        boolean result = userRegister(user);

        return result ? ResultVo.ok(null,"注册成功") : ResultVo.fail("注册失败");
    }

    @Override
    public ResultVo sendCode(String phone) {
        return createCode(phone, RedisConstants.LOGIN_CODE_KEY, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVo login(LoginFormDTO loginFormDTO) {
        // 获取登录类型
        Integer loginType = loginFormDTO.getLoginType();
        if (loginType == null) {
            return ResultVo.fail("非正常登录请求");
        }
        // 账号密码登录
        if (loginType == 0) {
            return usernamePasswordLogin(loginFormDTO.getUsername(), loginFormDTO.getPassword());
        }
        // 手机验证码登录
        if (loginType == 1) {
            return phoneCodeLogin(loginFormDTO.getPhone(), loginFormDTO.getCode());
        }
        return ResultVo.fail("非正常登录请求");
    }

    @Override
    public ResultVo setPassword(PwdFormDTO pwdFormDTO) {
        // 1. 获取登录用户
        UserDTO userDTO = UserHolder.getUser();
        // 2. 获取用户id
        Long userId = userDTO.getId();
        // 3. 查询用户
        User user = getById(userId);
        // 4. 判断用户是否存在
        if (user == null) {
            // 用户不存在
            UserHolder.removeUser();
            return ResultVo.fail("用户不存在");
        }
        // 5. 判断旧密码是否一致
        if (!passwordEncoder.matches(pwdFormDTO.getOldPassword(), user.getPassword())) {
            // 不一致
            return ResultVo.fail("旧密码错误");
        }
        // 6. 修改密码
        String password = passwordEncoder.encode(pwdFormDTO.getNewPassword());
        boolean result = update().set("password", password).eq("id", userId).update();

        // 7. 修改成功
        if (result) {
            // 删除系统用户相关信息
            stringRedisTemplate.delete(RedisConstants.LOGIN_KEY + user.getId());
        }

        return result ? ResultVo.ok(null,"修改密码成功") : ResultVo.fail("修改密码失败");
    }

    @Override
    public ResultVo updateIcon(String jwt, String icon) {
        if (StrUtil.isBlank(icon)) {
            return ResultVo.fail("头像不存在");
        }
        // 获取用户id
        UserDTO user = UserHolder.getUser();
        Long userId = user.getId();
        boolean result = update().set("icon", icon).eq("id", userId).update();
        if (result) {
            // 获取用户旧头像
            String oldIcon = user.getIcon();
            if (StrUtil.isNotBlank(oldIcon)) {
                UploadUtils.deleteFile(oldIcon);
            }
            stringRedisTemplate.opsForHash().put(RedisConstants.LOGIN_USER_KEY + jwt, "icon", icon);
            UserHolder.getUser().setIcon(icon);
        } else {
            UploadUtils.deleteFile(icon);
        }
        return result ? ResultVo.ok(null,"头像修改成功") : ResultVo.fail("头像修改失败");
    }

    @Override
    public ResultVo isNotUpdateUsername() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        User user = getById(userId);
        return user.getIsUpdate() < 1 ? ResultVo.ok() : ResultVo.fail();
    }

    @Override
    public ResultVo updateUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return ResultVo.fail("账号不能为空");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        User user = getById(userId);
        if (user.getIsUpdate() > 0) {
            return ResultVo.fail("您的用户名已超过修改次数，无法进行修改");
        }
        boolean result = update().set("username", username).set("isUpdate", 1).eq("id", userId).update();

        // 修改成功
        if (result) {
            // 删除系统用户相关信息
            stringRedisTemplate.delete(RedisConstants.LOGIN_KEY + user.getId());
        }

        return result ? ResultVo.ok("用户名修改成功！请重新登录") : ResultVo.fail("账号修改失败");
    }

    @Override
    public ResultVo updateNickName(String jwt, String nickname) {
        if (StrUtil.isBlank(nickname)) {
            return ResultVo.fail("昵称不能为空");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        boolean result = update().set("nickname", nickname).eq("id", userId).update();
        if (result) {
            stringRedisTemplate.opsForHash().put(RedisConstants.LOGIN_USER_KEY + jwt, "nickname", nickname);
            UserHolder.getUser().setNickname(nickname);
        }
        return result ? ResultVo.ok(null,"昵称修改成功") : ResultVo.fail("昵称修改失败");
    }

    @Override
    public ResultVo codePhone(String phone) {
        // 验证手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVo.fail("手机号格式错误");
        }
        // 获取用户手机号
        String userPhone = UserHolder.getUser().getPhone();
        if (phone.equals(userPhone)) {
            return ResultVo.fail("该手机号与当前绑定的手机号相同");
        }
        // 查询手机号是否注册
        Integer count = query().eq("phone", phone).count();
        if (count > 0) {
            return ResultVo.fail("手机号已注册");
        }
        // 发送验证码
        return createCode(phone, UPDATE_PHONE_CODE_KEY, UPDATE_PHONE_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVo updatePhone(String jwt, String phone, String code) {
        // 验证手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVo.fail("手机号格式错误");
        }
        // 判断验证码是否为空
        if (StrUtil.isBlank(code)) {
            return ResultVo.fail("验证码不能为空");
        }
        // 验证验证码
        ResultVo resultVo = checkCode(UPDATE_PHONE_CODE_KEY, phone, code);
        if (!resultVo.getSuccess()) {
            return resultVo;
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 修改手机号
        boolean result = update().set("phone", phone).eq("id", userId).update();
        if (result) {
            stringRedisTemplate.opsForHash().put(RedisConstants.LOGIN_USER_KEY + jwt, "phone", phone);
            UserHolder.getUser().setPhone(phone);
        }
        return result ? ResultVo.ok(null,"手机号修改成功") : ResultVo.fail("手机号修改失败");
    }

    @Override
    public ResultVo logOut(String token) {
        // 获得key
        String key = RedisConstants.LOGIN_USER_KEY + token;
        // 删除Redis中的用户信息
        stringRedisTemplate.delete(key);
        // 删除线程变量
        UserHolder.removeUser();

        // redis获取系统用户相关信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getUser().getId();
        // 获得key
        String key2 = RedisConstants.LOGIN_KEY + id;
        // 删除系统用户相关信息
        stringRedisTemplate.delete(key2);

        return ResultVo.ok();
    }

    @Override
    public ResultVo codePassword(String phone) {
        Integer count = query().eq("phone", phone).count();
        if (count <= 0) {
            return ResultVo.fail("手机号不存在");
        }
        return createCode(phone, RedisConstants.FORGOT_PASSWORD_CODE_KEY, RedisConstants.FORGOT_PASSWORD_CODE_TTL, TimeUnit.MINUTES);
    }

    @Override
    public ResultVo checkCodePassword(String phone, String code) {
        ResultVo resultVo = checkCode(RedisConstants.FORGOT_PASSWORD_CODE_KEY, phone, code);
        if (!resultVo.getSuccess()) {
            return resultVo;
        }
        // 向redis存入验证成功的手机号，并设置有效时间 30分钟
        stringRedisTemplate.opsForValue()
                .set(RedisConstants.FORGOT_PASSWORD_PHONE_KEY + phone , "1", RedisConstants.FORGOT_PASSWORD_PHONE_TTL, TimeUnit.MINUTES);
        return resultVo;
    }

    @Override
    public ResultVo updatePassword(PwdFormDTO pwdFormDTO) {
        // 获取手机号
        String phone = pwdFormDTO.getPhone();

        // 从redis中查询手机号，判断是否通过验证
        String checkResult = stringRedisTemplate.opsForValue().get(RedisConstants.FORGOT_PASSWORD_PHONE_KEY + phone);

        // 判断验证结果是否成功
        if (StrUtil.isBlank(checkResult)) {
            // 失败或者超时
            return ResultVo.fail("修改密码失败，请重新进行验证");
        }

        // 根据手机号查询用户
        User user = query().eq("phone", phone).one();

        if (user == null) {
            return ResultVo.fail("手机号不存在");
        }

        // 获取新密码
        String password = pwdFormDTO.getNewPassword();
        if (StrUtil.isBlank(password)) {
            return ResultVo.fail("密码不能为空");
        }

        String encode = passwordEncoder.encode(password);

        // 获取用户id
        Long userId = user.getId();
        // 修改密码
        boolean result = update().set("password", encode).eq("id", userId).update();
        stringRedisTemplate.delete(RedisConstants.FORGOT_PASSWORD_PHONE_KEY + phone);
        return result ? ResultVo.ok(null,"密码修改成功") : ResultVo.fail("密码修改失败");
    }

    @Override
    public ResultVo queryUser(UserQueryFromDTO userQueryFromDTO) {
        QueryWrapper<User> wrapper = queryUserWrapper(userQueryFromDTO);
        List<User> result = list(wrapper);
        return ResultVo.ok(result);
    }

    @Override
    public ResultVo queryUserList(Integer page, Integer size, UserQueryFromDTO userQueryFromDTO) {
        Page<User> userPage = new Page<>(page, size);
        userMapper.selectPage(userPage, queryUserWrapper(userQueryFromDTO));
        return ResultVo.ok(userPage);
    }

    @Override
    public ResultVo updateUser(User user) {
        // 获取用户id
        Long id = user.getId();
        // 查询用户是否存在
        if (getById(id) == null) {
            return ResultVo.fail("用户不存在");
        }
        boolean result = update(user, new UpdateWrapper<User>().eq("id", user.getId()));
        return result ? ResultVo.ok(null,"修改成功") : ResultVo.fail("修改失败");
    }

    @Override
    public ResultVo deleteUser(Long id) {
        // 查询用户是否存在
        if (getById(id) == null) {
            return ResultVo.fail("用户不存在");
        }
        // 删除用户
        boolean result = removeById(id);
        return result ? ResultVo.ok(null,"删除成功") : ResultVo.fail("删除失败");
    }

    @Override
    public ResultVo bulkDeleteUser(List<Long> ids) {
        // 删除成功数量
        int success = 0;
        for (Long id : ids) {
            // 查询用户是否存在
            if (getById(id) == null) {
                continue;
            }
            // 删除用户
            boolean result = removeById(id);
            if (result) {
                success++;
            }
        }
        return ResultVo.ok(null,"批量删除用户：" + success);
    }

    @Override
    public ResultVo signIn() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询用户是否已经签到
        boolean bit = getSignIn(userId);
        if (bit) {
            return ResultVo.ok(null,"今日已签到");
        }

        // 查询用户钱包
        UserAccount account = accountService.getById(userId);

        // 签到增加积分
        accountService.update().set("points", account.getPoints() + SystemConstants.SIGN_IN_POINTS).eq("id", userId).update();

        // 账单类型
        String type = BillType.SignIn.getName();
        // 账单记录金额
        String amount = String.valueOf(SystemConstants.SIGN_IN_POINTS);

        // 新增账单记录
        BillUtils billUtils = new BillUtils(stringRedisTemplate);
        billUtils.saveBill(userId, type, amount + "积分");

        // 用户签到
        setSignIn(userId);

        return ResultVo.ok(null,"签到成功");
    }

    @Override
    public ResultVo querySignIn() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询用户是否已经签到
        boolean result = getSignIn(userId);
        return result ? ResultVo.ok(null,"今日已签到") : ResultVo.fail("今日未签到");
    }

    @Override
    public ResultVo queryContinuityDay() {
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
            return ResultVo.ok(0);
        }

        Long number = result.get(0);

        if (number == null || number == 0) {
            return ResultVo.ok(0);
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

        return ResultVo.ok(count);
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
     * 条件查询用户
     * @param userQueryFromDTO 查询条件
     * @return QueryWrapper<User>
     */
    private QueryWrapper<User> queryUserWrapper(UserQueryFromDTO userQueryFromDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userQueryFromDTO != null) {
            // 判断是否根据id查询
            if (userQueryFromDTO.getId() != null) {
                queryWrapper.eq("id", userQueryFromDTO.getId());
            }
            // 判断是否根据用户名查询
            if (StrUtil.isNotBlank(userQueryFromDTO.getUsername())) {
                queryWrapper.eq("username", userQueryFromDTO.getUsername());
            }
            // 判断是否根据手机号查询
            if (StrUtil.isNotBlank(userQueryFromDTO.getPhone())) {
                queryWrapper.eq("phone", userQueryFromDTO.getUsername());
            }
            // 判断是否根据用户状态查询
            if (userQueryFromDTO.getStatus() != null) {
                queryWrapper.eq("status", userQueryFromDTO.getStatus());
            }
        }
        queryWrapper.eq("user_type", 1);
        return queryWrapper;
    }

    /**
     * 账号密码登录
     * @param username 用户名
     * @param password 密码
     * @return ResultVo
     */
    private ResultVo usernamePasswordLogin(String username, String password) {
        // 判断账号是否为空
        if (StrUtil.isBlank(username)) {
            return ResultVo.fail("账号不能为空");
        }

        // 判断是否无效账号
        if (RegexUtils.isUsernameInvalid(username)) {
            return ResultVo.fail("用户名格式错误！必须以字母开头，仅可包含字母、数字和下划线");
        }

        // 判断密码是否为空
        if (StrUtil.isBlank(password)) {
            return ResultVo.fail("密码不能为空");
        }

        // 交由spring security进行验证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();

        if (Objects.isNull(loginUser)) {
            return ResultVo.fail("账号或密码错误");
        }
        // 获取用户信息
        User user = loginUser.getUser();

        // 存在，将用户信息DTO写入Redis
        String token = saveUserToDTO(user);

        // 将系统用户相关信息存入Redis
        stringRedisTemplate.opsForValue()
                .set(RedisConstants.LOGIN_KEY + user.getId(), JSONUtil.toJsonStr(loginUser), RedisConstants.LOGIN_KEY_TTL, TimeUnit.MINUTES);

        // 返回token
        return ResultVo.ok(token, "登录成功");
    }

    /**
     * 手机验证码登录
     * @param phone 手机号
     * @param code 验证码
     * @return ResultVo
     */
    private ResultVo phoneCodeLogin(String phone, String code) {
        // 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVo.fail("手机号格式错误");
        }

        // 判断验证码是否为空
        if (StrUtil.isBlank(code)) {
            return ResultVo.fail("验证码不能为空");
        }

        // 交由将请求信息进行封装成token，并返回
        SmsCodeAuthenticationToken authenticationToken = new SmsCodeAuthenticationToken(phone, code);

        // 进行认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();

        if (Objects.isNull(loginUser)) {
            return ResultVo.fail("手机号或验证码错误");
        }
        // 获取用户信息
        User user = loginUser.getUser();

        // 存在，将用户信息DTO写入Redis
        String token = saveUserToDTO(user);

        // 将系统用户相关信息存入Redis
        stringRedisTemplate.opsForValue()
                .set(RedisConstants.LOGIN_KEY + user.getId(), JSONUtil.toJsonStr(loginUser), RedisConstants.LOGIN_KEY_TTL, TimeUnit.MINUTES);

        // 返回token
        return ResultVo.ok(token, "登录成功");
    }

    /**
     * 用户注册
     * @param user 注册用户信息
     * @return Boolean
     */
    private boolean userRegister(User user) {
        // 注册用户
        save(user);

        // 获取用户id
        Long id = user.getId();

        // 新建用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfoService.save(userInfo);

        // 新建用户钱包
        UserAccount userAccount = new UserAccount();
        userAccount.setId(id);
        accountService.save(userAccount);

        // 设置用户角色
        userRoleMapper.insert(new UserRole(user.getId(), 5L));

        return true;
    }

    /**
     * 生成手机验证码
     * @param phone 手机号
     * @param prefix key前缀
     * @param time 有效期限
     * @param timeUnit 时间单位
     * @return ResultVo
     */
    private ResultVo createCode(String phone, String prefix, Long time, TimeUnit timeUnit) {
        // 1. 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVo.fail("手机号格式错误");
        }
        // 2. 生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 3. 生成key
        String key = prefix + phone;
        // 4. 存入Redis，并设置有效期
        stringRedisTemplate.opsForValue().set(key, code, time, timeUnit);
        // 5. 发送验证码
        System.out.println("发送验证码成功！验证码：" + code);
        return ResultVo.ok(code);
    }

    /**
     * 验证验证码
     * @param prefix key前缀
     * @param phone 手机号
     * @param code 验证码
     * @return ResultVo
     */
    private ResultVo checkCode(String prefix, String phone, String code) {
        // 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVo.fail("手机号格式错误");
        }

        // 判断验证码是否为空
        if (StrUtil.isBlank(code)) {
            return ResultVo.fail("验证码不能为空");
        }

        String key = prefix + phone;
        // 从Redis中获取验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(key);
        // 判断验证码是否正确
        if (!code.equals(cacheCode)) {
            // 错误，返回错误信息
            return ResultVo.fail("验证码错误");
        }
        return ResultVo.ok(null,"验证通过");
    }


    /**
     * 保存用户
     */
    private String saveUserToDTO(User user) {
        // 生成token
        String jwt = JwtUtils.generateJwt(user.getId());

        // 设置Redis的key
        String key = RedisConstants.LOGIN_USER_KEY + jwt;

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
}
