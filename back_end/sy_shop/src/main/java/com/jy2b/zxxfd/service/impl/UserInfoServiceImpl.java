package com.jy2b.zxxfd.service.impl;

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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public ResultVo queryInfoMe() {
        // 获取用户
        UserDTO user = UserHolder.getUser();
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
        return updateInfo(userInfo, info, false);
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
        return updateInfo(userInfo, info, true);
    }

    /**
     * 修改用户信息
     * @param userInfo 要修改的用户信息
     * @param info 原来的用户信息
     * @param isAdmin 是否管理员修改
     * @return ResultVo
     */
    private ResultVo updateInfo(UserInfo userInfo, UserInfo info, boolean isAdmin) {
        UpdateWrapper<UserInfo> wrapper = new UpdateWrapper<>();

        // 判断年龄是否不为空，且不等于原来的年龄
        if (userInfo.getAge() != null && userInfo.getAge() != info.getAge()) {
            wrapper.set("age", userInfo.getAge());
        }
        // 判断邮箱是否不为空，且不等于原来的邮箱
        if (StrUtil.isNotBlank(userInfo.getEmail()) && !userInfo.getEmail().equals(info.getEmail())) {
            wrapper.set("email", userInfo.getEmail());
        }
        // 判断性别是否不为空，且不等于原来的性别
        if (StrUtil.isNotBlank(userInfo.getGender()) && !userInfo.getGender().equals(info.getGender())) {
            wrapper.set("gender", userInfo.getGender());
        }
        // 判断用户名是否不为空，且不等于原来的用户名
        if (StrUtil.isNotBlank(userInfo.getFullName()) && !userInfo.getFullName().equals(info.getFullName())) {
            wrapper.set("username", userInfo.getFullName());
        }
        // 判断用户qq是否不为空，且不等于原来的qq
        if (StrUtil.isNotBlank(userInfo.getQq()) && !userInfo.getQq().equals(info.getQq())) {
            wrapper.set("qq", userInfo.getQq());
        }
        // 判断用户生日是否不为空，且不等于原来的生日
        if (userInfo.getBirthday() != null && userInfo.getBirthday() != info.getBirthday()) {
            wrapper.set("birthday", userInfo.getBirthday());
        }
        // 判断用户等级是否不为空，且不等于原来的等级
        if (isAdmin && userInfo.getLevel() != null && userInfo.getLevel() != info.getLevel()) {
            wrapper.set("level", userInfo.getLevel());
        }
        wrapper.eq("id", userInfo.getId());
        // 修改用户信息
        boolean result = update(wrapper);
        // 返回信息
        return result ? ResultVo.ok("用户信息修改成功") : ResultVo.fail("用户信息修改失败");
    }
}
