package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.UserDTO;
import com.jy2b.zxxfd.domain.UserInfo;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.UserInfoMapper;
import com.jy2b.zxxfd.service.IUserInfoService;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jy2b.zxxfd.contants.RedisConstants.*;

/**
 * @author 林武泰
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVO queryInfoMe() {
        // 获取用户
        UserDTO user = UserHolder.getUser();

        // 查询redis
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(USER_INFO_KEY + user.getId());
        if (!map.isEmpty()) {
            // 将Map转为Bean
            UserInfo userInfo = BeanUtil.fillBeanWithMap(map, new UserInfo(), false);
            return ResultVO.ok(userInfo, "查询成功");
        }

        // 查询用户信息
        UserInfo userInfo = getById(user.getId());
        // 判断是否为空
        if (userInfo == null) {
            UserInfo info = new UserInfo();
            // 新建用户信息
            info.setId(user.getId());
            // 保存用户信息
            save(info);
            userInfo = info;
        }
        saveInfo(userInfo.getId());
        // 返回用户信息
        return ResultVO.ok(userInfo, "查询成功");
    }

    @Override
    public ResultVO updateInfo(UserInfo userInfo) {
        // 获取用户id
        Long id = UserHolder.getUser().getId();
        // 获取用户原本的信息
        UserInfo info = getById(id);
        // 判断用户信息是否存在
        if (info == null) {
            return ResultVO.fail("用户信息不存在");
        }
        // 设置用户id
        userInfo.setId(id);
        // 修改用户信息
        return updateInfo(userInfo, false);
    }

    @Override
    public ResultVO queryUserInfoById(Long userId) {
        // 根据用户ID查询用户信息
        UserInfo userInfo = getById(userId);
        // 判断用户信息是否存在
        if (userInfo == null) {
            return ResultVO.fail("用户信息不存在");
        }
        // 获取登录注册
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(LOGIN_REGISTER_KEY + userId);

        // 获取禁用状态
        String blockUpMsg = stringRedisTemplate.opsForValue().get(BLOCK_UP_USER_KEY + userId);

        Map<String, Object> resultMap = new HashMap<>();
        if (!map.isEmpty()) {
            resultMap.put("userInfo", userInfo);
        }
        resultMap.put("loginInfo", map);
        if (StrUtil.isNotBlank(blockUpMsg)) {
            Long expire = stringRedisTemplate.getExpire(BLOCK_UP_USER_KEY + userId, TimeUnit.MINUTES);

            Map<String, Object> blockUpMap = new HashMap<>();
            blockUpMap.put("message", blockUpMsg);
            blockUpMap.put("expire", expire);
            blockUpMap.put("timeUnit", "分钟");

            resultMap.put("blockUpInfo", blockUpMap);
        }

        return ResultVO.ok(resultMap, "查询成功");
    }

    @Override
    public ResultVO updateInfoAdmin(UserInfo userInfo) {
        // 获取用户原本的信息
        UserInfo info = getById(userInfo.getId());
        // 判断是否存在
        if (info == null) {
            return ResultVO.fail("用户信息不存在");
        }
        // 修改用户信息
        return updateInfo(userInfo, true);
    }

    /**
     * 修改用户信息
     * @param userInfo 要修改的用户信息
     * @param isAdmin 是否管理员修改
     * @return ResultVO
     */
    private ResultVO updateInfo(UserInfo userInfo, boolean isAdmin) {
        // 判断是否管理员
        if (!isAdmin) {
            userInfo.setLevel(null);
        }

        if (userInfo.getId() == null) {
            return ResultVO.fail("获取用户id错误");
        }

        // 修改用户信息
        boolean result = updateById(userInfo);

        if (result) {
            // 修改redis用户信息
            saveInfo(userInfo.getId());
        }

        // 返回信息
        return result ? ResultVO.ok(null, "用户信息修改成功") : ResultVO.fail("用户信息修改失败");
    }

    private void saveInfo(Long id) {
        // 获取用户id
        UserInfo userInfo = getById(id);
        // 拼接key
        String key = USER_INFO_KEY + id;
        // 将Bean转为Map
        Map<String, Object> map = BeanUtil.beanToMap(userInfo, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> {
                            if (fieldValue == null) {
                                fieldValue = "";
                            } else {
                                fieldValue += "";
                            }
                            return fieldValue;
                        }));
        // 保存入Redis
        stringRedisTemplate.opsForHash().putAll(key, map);
    }
}
