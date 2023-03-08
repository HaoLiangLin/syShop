package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Role;
import com.jy2b.zxxfd.domain.User;
import com.jy2b.zxxfd.domain.UserRole;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.RoleMapper;
import com.jy2b.zxxfd.mapper.UserMapper;
import com.jy2b.zxxfd.mapper.UserRoleMapper;
import com.jy2b.zxxfd.service.IUserRoleService;
import com.jy2b.zxxfd.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public ResultVO selectUserByRoleId(Long roleId) {
        // 根据角色Id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 根据角色Id查询用户
        List<UserRole> userRoleList = query().select("user_id").eq("role_id", roleId).list();
        // 判断是否拥有用户
        if (userRoleList.isEmpty()) {
            return ResultVO.ok(Collections.emptyList(), "查询成功");
        }
        // 获取全部角色ID
        List<Long> userIds = userRoleList.stream().map(UserRole::getUserId).collect(Collectors.toList());
        // 根据用户ID批量查询用户
        List<User> users = userMapper.selectBatchIds(userIds);

        return ResultVO.ok(users, "查询成功");
    }

    @Override
    public ResultVO selectRoleByUserId(Long userId) {
        // 根据用户Id查询用户
        User user = redisUtils.queryWithPassThrough("user", userId, User.class, val -> userMapper.selectById(val), null, null);
        // 判断用户是否存在
        if (user == null) {
            return ResultVO.fail("用户不存在");
        }
        // 查询用户角色
        UserRole userRole = query().eq("user_id", userId).one();
        // 判断用户角色是否存在
        if (userRole == null) {
            return ResultVO.fail("该用户未设置角色");
        }

        // 获取角色id
        Long roleId = userRole.getRoleId();
        // 根据角色Id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        return ResultVO.ok(role, "查询成功");
    }

    @Override
    public ResultVO delUserByRoleId(Long userId) {
        // 根据用户Id查询用户
        User user = redisUtils.queryWithPassThrough("user", userId, User.class, val -> userMapper.selectById(val), null, null);
        // 判断用户是否存在
        if (user == null) {
            return ResultVO.fail("用户不存在");
        }
        // 判断用户是否拥有角色
        UserRole userRole = query().eq("user_id", userId).one();
        if (userRole == null) {
            return ResultVO.fail("该用户未设置角色");
        }

        // 获取角色id
        Long roleId = userRole.getRoleId();
        // 根据角色Id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            // 删除无效角色
            remove(new QueryWrapper<UserRole>().eq("user_id", userId).eq("role_id", roleId));
            return ResultVO.fail("设置角色无效");
        }

        // 移除用户角色
        boolean removeResult = remove(new QueryWrapper<UserRole>().eq("user_id", userId));
        return removeResult ? ResultVO.ok("移除用户角色成功") : ResultVO.fail("移除用户角色失败");
    }

    @Override
    public ResultVO updateUserRole(Long userId, Long roleId) {
        // 根据用户Id查询用户
        User user = redisUtils.queryWithPassThrough("user", userId, User.class, val -> userMapper.selectById(val), null, null);
        // 判断用户是否存在
        if (user == null) {
            return ResultVO.fail("用户不存在");
        }
        // 判断角色是否设置用户
        UserRole userRole = query().eq("user_id", userId).one();
        if (userRole == null) {
            return ResultVO.fail("该用户未设置角色");
        } else {
            boolean remove = remove(new QueryWrapper<UserRole>().eq("user_id", userRole.getUserId()).eq("role_id", userRole.getRoleId()));
            if (!remove) {
                return ResultVO.fail("移除用户角色失败");
            }
        }
        // 根据角色Id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 修改用户角色
        boolean saveResult = save(new UserRole(userId, roleId));
        if (!saveResult) {
            throw new RuntimeException("移除用户角色失败");
        }
        return ResultVO.ok("修改用户角色成功");
    }

    @Override
    public ResultVO saveUserRole(Long userId, Long roleId) {
        // 根据用户Id查询用户
        User user = redisUtils.queryWithPassThrough("user", userId, User.class, val -> userMapper.selectById(val), null, null);
        // 判断用户是否存在
        if (user == null) {
            return ResultVO.fail("用户不存在");
        }
        // 判断角色是否设置用户
        UserRole userRole = query().eq("user_id", userId).one();
        if (userRole != null) {
            return ResultVO.fail("该用户已设置角色");
        }
        // 根据角色Id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 新增用户角色
        boolean saveResult = save(new UserRole(userId, roleId));
        return saveResult ? ResultVO.ok("新增用户角色成功") : ResultVO.fail("新增用户角色失败");
    }
}
