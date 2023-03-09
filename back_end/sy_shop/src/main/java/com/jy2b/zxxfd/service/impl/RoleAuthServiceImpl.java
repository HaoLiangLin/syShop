package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Auth;
import com.jy2b.zxxfd.domain.Role;
import com.jy2b.zxxfd.domain.RoleAuth;
import com.jy2b.zxxfd.domain.dto.RoleAuthDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.AuthMapper;
import com.jy2b.zxxfd.mapper.RoleAuthMapper;
import com.jy2b.zxxfd.mapper.RoleMapper;
import com.jy2b.zxxfd.service.IRoleAuthService;
import com.jy2b.zxxfd.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 林武泰
 */
@Service
public class RoleAuthServiceImpl extends ServiceImpl<RoleAuthMapper, RoleAuth> implements IRoleAuthService {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private AuthMapper authMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public ResultVO selectAuthByRoleId(Long roleId) {
        // 根据角色id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 根据角色id查询权限
        List<RoleAuth> roleAuths = query().select("auth_id").eq("role_id", roleId).list();
        // 判断是否拥有权限
        if (roleAuths.isEmpty()) {
            return ResultVO.ok(Collections.emptyList(), "查询成功");
        }

        // 获取全部的权限id
        List<Long> authIds = roleAuths.stream().map(RoleAuth::getAuthId).collect(Collectors.toList());
        // 根据权限id批量查询权限
        List<Auth> authList = authMapper.selectBatchIds(authIds);

        return ResultVO.ok(authList, "查询成功");
    }

    @Override
    public ResultVO selectRoleByAuthId(Long authId) {
        // 根据权限id查询权限
        Auth auth = redisUtils.queryWithPassThrough("auth", authId, Auth.class, val -> authMapper.selectById(val), null, null);
        // 判断权限是否存在
        if (auth == null) {
            return ResultVO.fail("权限不存在");
        }
        // 根据权限id查询角色
        List<RoleAuth> roleAuths = query().select("role_id").eq("auth_id", authId).list();
        // 判断是否拥有权限
        if (roleAuths.isEmpty()) {
            return ResultVO.ok(Collections.emptyList(), "查询成功");
        }

        // 获取全部角色id
        List<Long> roleIds = roleAuths.stream().map(RoleAuth::getRoleId).collect(Collectors.toList());
        // 根据角色id批量查询角色
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return ResultVO.ok(roles, "查询成功");
    }

    @Override
    public ResultVO delAuthByRoleId(Long roleId, Long authId) {
        // 根据角色id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 根据权限id查询权限
        Auth auth = redisUtils.queryWithPassThrough("auth", authId, Auth.class, val -> authMapper.selectById(val), null, null);
        // 判断权限是否存在
        if (auth == null) {
            return ResultVO.fail("权限不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("roleName", role.getName());
        map.put("authName", auth.getName());
        // 删除角色权限
        boolean removeResult = remove(new QueryWrapper<RoleAuth>().eq("role_id", roleId).eq("auth_id", authId));
        return removeResult ? ResultVO.ok(map, "删除角色权限成功") : ResultVO.fail("删除角色权限失败");
    }

    @Override
    public ResultVO delAllAuthByRoleId(Long roleId) {
        // 根据角色id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 根据角色id查询权限
        List<RoleAuth> roleAuths = query().select("auth_id").eq("role_id", roleId).list();
        // 判断是否拥有权限
        if (roleAuths.isEmpty()) {
            return ResultVO.fail("该角色未拥有权限");
        }

        // 获取全部的权限id
        List<Long> authIds = roleAuths.stream().map(RoleAuth::getAuthId).collect(Collectors.toList());
        // 批量移除角色权限
        boolean result = remove(new QueryWrapper<RoleAuth>().in("auth_id", authIds));

        return result ? ResultVO.ok("移除角色权限成功") : ResultVO.fail("移除角色权限失败");
    }

    @Override
    public ResultVO delAllRoleByAuthId(Long authId) {
        // 根据权限id查询权限
        Auth auth = redisUtils.queryWithPassThrough("auth", authId, Auth.class, val -> authMapper.selectById(val), null, null);
        // 判断权限是否存在
        if (auth == null) {
            return ResultVO.fail("权限不存在");
        }
        // 根据权限id查询角色
        List<RoleAuth> roleAuths = query().select("role_id").eq("auth_id", authId).list();
        // 判断是否拥有权限
        if (roleAuths.isEmpty()) {
            return ResultVO.fail("权限未被角色设置");
        }

        // 获取全部角色id
        List<Long> roleIds = roleAuths.stream().map(RoleAuth::getRoleId).collect(Collectors.toList());
        // 批量移除权限角色
        boolean result = remove(new QueryWrapper<RoleAuth>().in("role_id", roleIds));
        return result ? ResultVO.ok("移除权限角色成功") : ResultVO.fail("移除权限角色失败");
    }

    @Override
    public ResultVO saveRoleAuth(Long roleId, Long authId) {
        // 根据角色id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 根据权限id查询权限
        Auth auth = redisUtils.queryWithPassThrough("auth", authId, Auth.class, val -> authMapper.selectById(val), null, null);
        // 判断权限是否存在
        if (auth == null) {
            return ResultVO.fail("权限不存在");
        }
        // 判断权限是否可用
        if (auth.getStatus() == 0) {
            return ResultVO.fail("权限不可用");
        }

        // 新增角色权限
        boolean saveResult = save(new RoleAuth(roleId, authId));

        return saveResult ? ResultVO.ok("新增角色权限成功") : ResultVO.fail("新增角色权限失败");
    }

    @Override
    public ResultVO batchSaveRoleAuth(RoleAuthDTO roleAuthDTO) {
        // 获取角色id
        Long roleId = roleAuthDTO.getRoleId();
        // 根据角色id查询角色
        Role role = redisUtils.queryWithPassThrough("role", roleId, Role.class, val -> roleMapper.selectById(val), null, null);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }
        // 获取权限ids
        List<Long> authIds = roleAuthDTO.getAuthIds();
        // 判断权限ids是否存在
        if (authIds.isEmpty()) {
            return ResultVO.fail("权限不能为空");
        }
        // 过滤每个权限id
        List<Long> authIdList = authIds.stream().filter(authId -> {
            // 根据权限id查询权限
            Auth auth = authMapper.selectById(authId);
            // 判断权限是否存在
            return auth != null && auth.getStatus() != 0;
        }).collect(Collectors.toList());
        // 判断是否存在可用权限
        if (authIdList.isEmpty()) {
            return ResultVO.fail("权限不可用");
        }
        // 批量新增权限
        List<RoleAuth> roleAuthList = authIdList.stream().map(authId -> new RoleAuth(roleId, authId)).collect(Collectors.toList());
        boolean saveResult = saveBatch(roleAuthList);
        return saveResult ? ResultVO.ok("批量新增成功") : ResultVO.fail("批量新增失败");
    }
}
