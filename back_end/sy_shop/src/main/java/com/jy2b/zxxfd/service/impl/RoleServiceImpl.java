package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Role;
import com.jy2b.zxxfd.domain.RoleAuth;
import com.jy2b.zxxfd.domain.UserRole;
import com.jy2b.zxxfd.domain.dto.RoleManageDTO;
import com.jy2b.zxxfd.domain.dto.RoleSaveDTO;
import com.jy2b.zxxfd.domain.dto.RoleUpdateDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.RoleAuthMapper;
import com.jy2b.zxxfd.mapper.RoleMapper;
import com.jy2b.zxxfd.mapper.UserRoleMapper;
import com.jy2b.zxxfd.service.IRoleService;
import com.jy2b.zxxfd.utils.RedisUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jy2b.zxxfd.contants.RedisConstants.ROLE_LABEL_KEY;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RoleAuthMapper roleAuthMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public ResultVO labelList() {
        // 查询Redis
        String jsonLabel = stringRedisTemplate.opsForValue().get(ROLE_LABEL_KEY);
        // 判断是否不为空
        if (StrUtil.isNotBlank(jsonLabel)) {
            // 将结果转为集合
            JSONArray jsonArray = JSONUtil.parseArray(jsonLabel);
            List<String> list = JSONUtil.toList(jsonArray, String.class);
            return ResultVO.ok(list, "查询成功");
        }

        // 查询角色标签
        List<Role> roleList = query().select("DISTINCT label").list();
        // 判断是否不为空
        if (!roleList.isEmpty()) {
            List<String> list = new ArrayList<>();
            roleList.forEach(role -> list.add(role.getLabel()));

            // 将集合转为JSON字符串
            String jsonStr = JSONUtil.toJsonStr(list);
            // 将角色标签存入Redis
            stringRedisTemplate.opsForValue().set(ROLE_LABEL_KEY, jsonStr);

            return ResultVO.ok(list, "查询成功");
        }
        return ResultVO.ok(Collections.emptyList(), "查询成功");
    }

    @Override
    public ResultVO nameByLabelList(String label) {
        // 判断权限标签是否为空
        if (StrUtil.isBlank(label)) {
            return ResultVO.fail("用户类型不能为空");
        }
        // 根据权限标签查询权限
        List<Role> roleList = query().eq("label", label).list();
        // 判断权限是否存在
        if (!roleList.isEmpty()) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            roleList.forEach(role -> {
                Map<String, Object> stringMap = new HashMap<>();
                stringMap.put("id", role.getId());
                stringMap.put("name", role.getName());
                mapList.add(stringMap);
            });

            return ResultVO.ok(mapList, "查询成功");
        }

        return ResultVO.ok(Collections.emptyList(), "查询成功");
    }

    @Override
    public ResultVO findRole(Integer page, Integer size, RoleManageDTO roleManageDTO) {
        Page<Role> rolePage = new Page<>(page, size);
        // 生成查询构造器
        QueryWrapper<Role> roleQueryWrapper = selectRole(roleManageDTO);
        roleMapper.selectPage(rolePage, roleQueryWrapper);
        return ResultVO.ok(rolePage, "查询成功");
    }

    @Override
    public ResultVO updateRole(RoleUpdateDTO roleUpdateDTO) {
        boolean isUpdateLabel = false;

        // 获取角色id
        Long roleId = roleUpdateDTO.getId();
        // 判断角色id是否为空
        if (roleId == null) {
            return ResultVO.fail("角色id不能为空");
        }
        // 查询角色
        Role role = getById(roleId);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }

        UpdateWrapper<Role> roleUpdateWrapper = new UpdateWrapper<>();
        // 获取角色名称
        String name = roleUpdateDTO.getName();
        // 判断角色名称是否不为空
        if (StrUtil.isNotBlank(name)) {
            // 判断权限名称是否存在
            Integer nameCount = query().eq("name", name).count();
            if (nameCount > 0) {
                return ResultVO.fail("角色名称已存在");
            }
            roleUpdateWrapper.set("name", name);
        }

        // 获取角色标识
        String perms = roleUpdateDTO.getPerms();
        // 判断角色标识是否不为空
        if (StrUtil.isNotBlank(perms)) {
            // 判断权限标识是否存在
            Integer permsCount = query().eq("perms", perms).count();
            if (permsCount > 0) {
                return ResultVO.fail("角色标识已存在");
            }
            // 添加权限标识条件
            roleUpdateWrapper.set("perms", perms);
        }

        // 获取角色状态
        Integer status = roleUpdateDTO.getStatus();
        // 判断角色状态是否为空
        if (status != null) {
            // 添加角色状态条件
            if (status == 0 || status == 1) {
                roleUpdateWrapper.set("status", status);
            }
        }

        // 获取角色标签
        String label = roleUpdateDTO.getLabel();
        // 判断角色标签是否为空
        if (StrUtil.isNotBlank(label)) {
            // 添加角色标签条件
            roleUpdateWrapper.set("label", label);
            isUpdateLabel = true;
        }

        // 获取角色备注
        String remark = roleUpdateDTO.getRemark();
        // 判断角色备注是否不为空
        if (StrUtil.isNotBlank(remark)) {
            // 添加角色备注条件
            roleUpdateWrapper.set("remark", remark);
        }

        // 添加角色ID条件
        roleUpdateWrapper.eq("id", roleId);

        // 修改角色
        boolean updateResult = update(roleUpdateWrapper);
        if (updateResult) {
            // 判断是否修改权限标签
            if (isUpdateLabel) {
                // 更新权限标签缓存
                updateLabelCache();
            }
        }

        return updateResult ? ResultVO.ok("修改角色成功") : ResultVO.fail("修改角色失败");
    }

    @Override
    public ResultVO saveRole(RoleSaveDTO roleSaveDTO) {
        // 获取角色名称
        String name = roleSaveDTO.getName();
        // 判断角色名称是否为空
        if (StrUtil.isBlank(name)) {
            return ResultVO.fail("角色名称不能为空");
        }
        // 获取角色标识
        String perms = roleSaveDTO.getPerms();
        // 判断角色标识是否为空
        if (StrUtil.isBlank(perms)) {
            return ResultVO.fail("角色标识不能为空");
        }
        // 获取角色标签
        String label = roleSaveDTO.getLabel();
        if (StrUtil.isBlank(label)) {
            return ResultVO.fail("角色标签不能为空");
        }
        // 获取角色备注
        String remark = roleSaveDTO.getRemark();

        // 判断角色名称是否存在
        Integer nameCount = query().eq("name", name).count();
        if (nameCount > 0) {
            return ResultVO.fail("角色名称已存在");
        }
        // 判断角色标识是否存在
        Integer permsCount = query().eq("perms", perms).count();
        if (permsCount > 0) {
            return ResultVO.fail("角色标识已存在");
        }

        // 新增角色
        Role role = new Role();
        role.setName(name);
        role.setPerms(perms);
        role.setLabel(label);
        role.setRemark(remark);

        boolean saveResult = save(role);
        if (saveResult) {
            // 获取缓存中的标签
            String jsonLabel = stringRedisTemplate.opsForValue().get(ROLE_LABEL_KEY);
            // 将字符串转为集合
            List<String> labels = JSONUtil.toList(jsonLabel, String.class);
            // 判断新增标签是否已存在
            int size = (int) labels.stream().filter(l -> l.equals(label)).count();
            if (size < 1) {
                // 更新缓存中的标签
                updateLabelCache();
            }
        }

        return saveResult ? ResultVO.ok(role, "添加角色成功") : ResultVO.fail("添加角色失败");
    }

    @Resource
    private RedisUtils redisUtils;

    @Override
    public ResultVO delRole(Long id) {
        // 查询角色
        Role role = getById(id);
        // 判断角色是否存在
        if (role == null) {
            return ResultVO.fail("角色不存在");
        }

        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("role_id", id);
        // 判断角色是否已设置用户
        Integer userRoleCount = userRoleMapper.selectCount(userRoleQueryWrapper);
        if (userRoleCount > 0) {
            return ResultVO.fail("已有用户设置为该角色，暂无法删除");
        }

        QueryWrapper<RoleAuth> roleAuthQueryWrapper = new QueryWrapper<>();
        roleAuthQueryWrapper.eq("role_id", id);
        // 判断角色是否已设置权限
        Integer count = roleAuthMapper.selectCount(roleAuthQueryWrapper);
        if (count > 0) {
            // 删除该角色设置
            int delete = roleAuthMapper.delete(roleAuthQueryWrapper);
            if (delete < 1) {
                // 删除失败，进行报错回滚
                throw new RuntimeException("删除角色失败");
            }
        }

        // 删除角色
        boolean removeResult = removeById(id);
        if (!removeResult) {
            // 删除失败，进行报错回滚
            throw new RuntimeException("删除权限失败");
        }
        // 更新权限标签
        updateLabelCache();
        // 放入回收站
        redisUtils.setRecycleBin(role, role.getName(), null, null);
        return ResultVO.ok(role, "删除权限成功");
    }

    /**
     * 更新角色标签缓存
     */
    public void updateLabelCache() {
        // 查询权限标签
        List<Role> labelList = query().select("DISTINCT label").list();
        // 判断是否不为空
        if (!labelList.isEmpty()) {
            List<String> list = new ArrayList<>();
            labelList.forEach(label -> list.add(label.getLabel()));
            // 将集合转为JSON字符串
            String jsonStr = JSONUtil.toJsonStr(list);
            // 存入Redis
            stringRedisTemplate.opsForValue().set(ROLE_LABEL_KEY, jsonStr);
        }
        // 设置空缓存
        stringRedisTemplate.opsForValue().set(ROLE_LABEL_KEY, "", 1L, TimeUnit.MINUTES);
    }
    /**
     * 生成角色查询条件构造器
     * @param roleManageDTO 角色查询条件
     * @return QueryWrapper<Role>
     */
    private QueryWrapper<Role> selectRole(RoleManageDTO roleManageDTO) {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        // 判断条件是否为空
        if (roleManageDTO == null) {
            return roleQueryWrapper;
        }
        // 获取角色id
        Long roleId = roleManageDTO.getId();
        // 判断角色id是否不为空
        if (roleId != null) {
            // 添加角色ID条件
            roleQueryWrapper.eq("id", roleId);
        }

        // 获取角色名称
        String name = roleManageDTO.getName();
        // 判断角色名称是否不为空
        if (StrUtil.isNotBlank(name)) {
            // 添加角色名称条件
            roleQueryWrapper.eq("name", name);
        }

        // 获取角色标识
        String perms = roleManageDTO.getPerms();
        // 判断角色标识是否不为空
        if (StrUtil.isNotBlank(perms)) {
            // 添加角色标识条件
            roleQueryWrapper.eq("perms", perms);
        }

        // 获取角色状态
        Integer status = roleManageDTO.getStatus();
        // 判断角色状态是否不为空
        if (status != null) {
            // 添加角色状态条件
            if (status == 0 || status == 1) {
                roleQueryWrapper.eq("status", status);
            }
        }

        // 获取角色标签
        String label = roleManageDTO.getLabel();
        // 判断角色标签是否不为空
        if (StrUtil.isNotBlank(label)) {
            roleQueryWrapper.eq("label", label);
        }


        return roleQueryWrapper;
    }
}
