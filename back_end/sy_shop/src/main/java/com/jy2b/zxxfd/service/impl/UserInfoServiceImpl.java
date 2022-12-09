package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.dto.UserDTO;
import com.jy2b.zxxfd.domain.UserInfo;
import com.jy2b.zxxfd.mapper.UserInfoMapper;
import com.jy2b.zxxfd.service.IUserInfoService;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jy2b.zxxfd.contants.RedisConstants.USER_INFO_KEY;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVo queryInfoMe() {
        // 获取用户
        UserDTO user = UserHolder.getUser();

        // 查询redis
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(USER_INFO_KEY + user.getId());
        if (!map.isEmpty()) {
            // 将Map转为Bean
            UserInfo userInfo = BeanUtil.fillBeanWithMap(map, new UserInfo(), false);
            return ResultVo.ok(userInfo);
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
        return ResultVo.ok(userInfo);
    }

    @Override
    public ResultVo updateInfo(UserInfo userInfo) {
        // 获取用户id
        Long id = UserHolder.getUser().getId();
        // 获取用户原本的信息
        UserInfo info = getById(id);
        // 判断用户信息是否存在
        if (info == null) {
            return ResultVo.fail("用户信息不存在");
        }
        // 设置用户id
        userInfo.setId(id);
        // 修改用户信息
        return updateInfo(userInfo, false);
    }

    @Override
    public ResultVo queryInfo(UserInfo userInfo) {
        QueryWrapper<UserInfo> queryWrapper = queryUserInfoWrapper(userInfo);
        List<UserInfo> result = list(queryWrapper);
        return ResultVo.ok(result);
    }

    @Override
    public ResultVo queryInfoAll(Integer page, Integer number, UserInfo userInfo) {
        // 定义分页
        Page<UserInfo> userInfoPage = new Page<>(page, number);

        // 根据分页页数与分页条数查询用户信息
        userInfoMapper.selectPage(userInfoPage, userInfo != null ? queryUserInfoWrapper(userInfo) : null);
        // 返回信息
        return ResultVo.ok(userInfoPage);
    }

    private QueryWrapper<UserInfo> queryUserInfoWrapper(UserInfo userInfo) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        // 判断id是否不为空
        if (userInfo.getId() != null) {
            queryWrapper.eq("id", userInfo.getId());
        }
        // 判断性别是否不为空
        if (StrUtil.isNotBlank(userInfo.getGender())) {
            queryWrapper.eq("gender", userInfo.getGender());
        }
        // 判断等级是否不为空
        if (userInfo.getLevel() != null) {
            queryWrapper.eq("level", userInfo.getLevel());
        }
        return queryWrapper;
    }

    @Override
    public ResultVo updateInfoAdmin(UserInfo userInfo) {
        // 获取用户原本的信息
        UserInfo info = getById(userInfo.getId());
        // 判断是否存在
        if (info == null) {
            return ResultVo.fail("用户信息不存在");
        }
        // 修改用户信息
        return updateInfo(userInfo, true);
    }

    /**
     * 修改用户信息
     * @param userInfo 要修改的用户信息
     * @param isAdmin 是否管理员修改
     * @return ResultVo
     */
    private ResultVo updateInfo(UserInfo userInfo, boolean isAdmin) {
        // 判断是否管理员
        if (!isAdmin) {
            userInfo.setLevel(null);
        }

        if (userInfo.getId() == null) {
            return ResultVo.fail("获取用户id错误");
        }

        // 修改用户信息
        boolean result = updateById(userInfo);

        if (result) {
            // 修改redis用户信息
            saveInfo(userInfo.getId());
        }

        // 返回信息
        return result ? ResultVo.ok(null, "用户信息修改成功") : ResultVo.fail("用户信息修改失败");
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
