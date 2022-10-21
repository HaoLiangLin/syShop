package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jy2b.zxxfd.domain.LoginUser;
import com.jy2b.zxxfd.domain.User;
import com.jy2b.zxxfd.mapper.UserDetailsMapper;
import com.jy2b.zxxfd.mapper.UserMapper;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义逻辑控制认证
 * 根据唯一性的用户名查询登录用户信息和用户角色权限信息
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDetailsMapper userDetailsMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 根据用户名查询信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username).or().eq("phone", username);
        User user = userMapper.selectOne(userQueryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在,请前往注册！");
        }

        if (user.getStatus() == 1) {
            throw new LockedException("账号已停用！");
        }

        // 2. 查询权限信息
        List<String> perms = userDetailsMapper.queryAuthPermsByUserId(user.getId());

        // 3. 返回UserDetails
        return new LoginUser(user, perms);
    }
}
