package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Auth;
import com.jy2b.zxxfd.domain.RoleAuth;
import com.jy2b.zxxfd.domain.dto.AuthManageDTO;
import com.jy2b.zxxfd.domain.dto.AuthSaveDTO;
import com.jy2b.zxxfd.domain.dto.AuthUpdateDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.AuthMapper;
import com.jy2b.zxxfd.mapper.RoleAuthMapper;
import com.jy2b.zxxfd.service.IAuthService;
import com.jy2b.zxxfd.utils.RedisUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jy2b.zxxfd.contants.RedisConstants.AUTH_LABEL_KEY;

@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements IAuthService {
    @Resource
    private AuthMapper authMapper;

    @Resource
    private RoleAuthMapper roleAuthMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVO labelList() {
        // 查询Redis
        String jsonLabel = stringRedisTemplate.opsForValue().get(AUTH_LABEL_KEY);
        // 判断是否不为空
        if (StrUtil.isNotBlank(jsonLabel)) {
            // 将结果转为集合
            JSONArray jsonArray = JSONUtil.parseArray(jsonLabel);
            List<String> list = JSONUtil.toList(jsonArray, String.class);
            return ResultVO.ok(list, "查询成功");
        }

        // 查询权限标签
        List<Auth> labelList = query().select("DISTINCT label").list();
        // 判断是否不为空
        if (!labelList.isEmpty()) {
            List<String> list = new ArrayList<>();
            labelList.forEach(label -> list.add(label.getLabel()));
            // 将集合转为JSON字符串
            String jsonStr = JSONUtil.toJsonStr(list);
            // 存入Redis
            stringRedisTemplate.opsForValue().set(AUTH_LABEL_KEY, jsonStr);

            return ResultVO.ok(list, "查询成功");
        }
        // 返回空集合
        return ResultVO.ok(Collections.emptyList(), "查询成功");
    }

    @Override
    public ResultVO nameByLabelList(String label) {
        // 判断权限标签是否为空
        if (StrUtil.isBlank(label)) {
            return ResultVO.fail("权限类型不能为空");
        }
        // 根据权限标签查询权限
        List<Auth> authList = query().eq("label", label).list();
        // 判断权限是否存在
        if (!authList.isEmpty()) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            authList.forEach(auth -> {
                Map<String, Object> stringMap = new HashMap<>();
                stringMap.put("id", auth.getId());
                stringMap.put("name", auth.getName());
                mapList.add(stringMap);
            });

            return ResultVO.ok(mapList, "查询成功");
        }

        return ResultVO.ok(Collections.emptyList(), "查询成功");
    }

    @Override
    public ResultVO findAuth(Integer page, Integer size, AuthManageDTO authManageDTO) {
        Page<Auth> authPage = new Page<>(page, size);
        // 生成条件构造器
        QueryWrapper<Auth> authQueryWrapper = selectUser(authManageDTO);
        authMapper.selectPage(authPage, authQueryWrapper);
        return ResultVO.ok(authPage, "查询成功");
    }

    @Override
    public ResultVO updateAuth(AuthUpdateDTO authUpdateDTO) {
        boolean isUpdateLabel = false;

        // 获取权限id
        Long authID = authUpdateDTO.getId();
        // 判断权限是否为空
        if (authID == null) {
            return ResultVO.fail("权限ID不能为空");
        }
        // 查询权限是否存在
        Auth auth = getById(authID);
        if (auth == null) {
            return ResultVO.fail("权限不存在");
        }

        UpdateWrapper<Auth> authUpdateWrapper = new UpdateWrapper<>();
        // 获取权限名称
        String name = authUpdateDTO.getName();
        // 判断权限名称是否不为空
        if (StrUtil.isNotBlank(name)) {
            // 判断权限名称是否存在
            Integer nameCount = query().eq("name", name).count();
            if (nameCount > 0) {
                return ResultVO.fail("权限名称已存在");
            }
            // 添加权限名称条件
            authUpdateWrapper.set("name", name);
        }

        // 获取权限标识
        String perms = authUpdateDTO.getPerms();
        // 判断权限标识是否不为空
        if (StrUtil.isNotBlank(perms)) {
            // 判断权限标识是否存在
            Integer permsCount = query().eq("perms", perms).count();
            if (permsCount > 0) {
                return ResultVO.fail("权限标识已存在");
            }
            // 添加权限标识条件
            authUpdateWrapper.set("perms", perms);
        }

        // 获取权限状态
        Integer status = authUpdateDTO.getStatus();
        // 判断权限状态是否不为空
        if (status != null) {
            // 添加权限状态条件
            if (status == 0 || status == 1) {
                authUpdateWrapper.set("status", status);
            }
        }

        // 获取权限类型
        Integer type = authUpdateDTO.getType();
        // 判断权限类型是否不为空
        if (type != null) {
            // 判断是否有效类型
            if (type > 3 || type < 0) {
                return ResultVO.fail("权限类型无效，0：查询权限，1：写入权限，2：修改权限，3：删除权限");
            }
            // 添加权限类型条件
            authUpdateWrapper.set("type", type);
        }

        // 获取权限标签
        String label = authUpdateDTO.getLabel();
        // 判断权限标签是否不为空
        if (StrUtil.isNotBlank(label)) {
            // 添加权限标签条件
            authUpdateWrapper.set("label", label);
            isUpdateLabel = true;
        }

        // 获取权限备注
        String remark = authUpdateDTO.getRemark();
        // 判断权限备注是否不为空
        if (StrUtil.isNotBlank(remark)) {
            // 添加权限备注条件
            authUpdateWrapper.set("remark", remark);
        }

        // 条件权限id条件
        authUpdateWrapper.eq("id", authID);

        // 修改权限
        boolean updateResult = update(authUpdateWrapper);
        if (updateResult) {
            // 判断是否修改权限标签
            if (isUpdateLabel) {
                // 更新权限标签缓存
                updateLabelCache();
            }
        }
        return updateResult ? ResultVO.ok("修改权限成功") : ResultVO.fail("修改权限失败");
    }

    @Override
    public ResultVO saveAuth(AuthSaveDTO authSaveDTO) {
        // 获取权限名称
        String name = authSaveDTO.getName();
        // 判断权限名称是否为空
        if (StrUtil.isBlank(name)) {
            return ResultVO.fail("权限名称不能为空");
        }
        // 获取权限标识
        String perms = authSaveDTO.getPerms();
        // 判断权限标识是否为空
        if (StrUtil.isBlank(perms)) {
            return ResultVO.fail("权限标识不能为空");
        }
        // 获取权限类型
        Integer type = authSaveDTO.getType();
        // 判断权限类型是否为空
        if (type == null) {
            return ResultVO.fail("权限类型不能为空");
        }
        if (type > 3 || type < 0) {
            return ResultVO.fail("权限类型无效，0：查询权限，1：写入权限，2：修改权限，3：删除权限");
        }
        // 获取权限标签
        String label = authSaveDTO.getLabel();
        // 判断权限标签是否为空
        if (StrUtil.isBlank(label)) {
            return ResultVO.fail("权限标签不能为空");
        }
        // 获取权限备注
        String remark = authSaveDTO.getRemark();

        // 判断权限名称是否存在
        Integer nameCount = query().eq("name", name).count();
        if (nameCount > 0) {
            return ResultVO.fail("权限名称已存在");
        }
        // 判断权限标识是否存在
        Integer permsCount = query().eq("perms", perms).count();
        if (permsCount > 0) {
            return ResultVO.fail("权限标识已存在");
        }

        // 新增权限
        Auth auth = new Auth();
        auth.setName(name);
        auth.setPerms(perms);
        auth.setType(type);
        auth.setLabel(label);
        auth.setRemark(remark);

        boolean saveResult = save(auth);
        if (saveResult) {
            // 获取缓存中的标签
            String jsonLabel = stringRedisTemplate.opsForValue().get(AUTH_LABEL_KEY);
            // 将字符串转为集合
            List<String> labels = JSONUtil.toList(jsonLabel, String.class);
            // 判断新增标签是否已存在
            int size = (int) labels.stream().filter(l -> l.equals(label)).count();
            if (size < 1) {
                // 更新缓存中的标签
                updateLabelCache();
            }
        }
        return saveResult ? ResultVO.ok(auth, "添加权限成功") : ResultVO.fail("添加权限失败");
    }

    @Resource
    private RedisUtils redisUtils;

    @Override
    public ResultVO delAuth(Long id) {
        // 判断权限是否存在
        Auth auth = getById(id);
        if (auth == null) {
            return ResultVO.fail("权限不存在");
        }

        // 判断权限是否已被使用
        QueryWrapper<RoleAuth> authQueryWrapper = new QueryWrapper<>();
        authQueryWrapper.eq("auth_id", id);

        Integer count = roleAuthMapper.selectCount(authQueryWrapper);
        if (count > 0) {
            // 删除被使用权限
            int delete = roleAuthMapper.delete(authQueryWrapper);
            if (delete < 1) {
                // 删除失败，进行报错回滚
                throw new RuntimeException("删除权限失败");
            }
        }

        // 删除权限
        boolean removeResult = removeById(id);
        if (!removeResult) {
            // 删除失败，进行报错回滚
            throw new RuntimeException("删除权限失败");
        }
        // 更新权限标签
        updateLabelCache();
        // 放入回收站
        redisUtils.setRecycleBin(auth, auth.getName(), null, null);
        return ResultVO.ok(auth, "删除权限成功");
    }

    /**
     * 更新权限标签缓存
     */
    public void updateLabelCache() {
        // 查询权限标签
        List<Auth> labelList = query().select("DISTINCT label").list();
        // 判断是否不为空
        if (!labelList.isEmpty()) {
            List<String> list = new ArrayList<>();
            labelList.forEach(label -> list.add(label.getLabel()));
            // 将集合转为JSON字符串
            String jsonStr = JSONUtil.toJsonStr(list);
            // 存入Redis
            stringRedisTemplate.opsForValue().set(AUTH_LABEL_KEY, jsonStr);
        }
        // 设置空缓存
        stringRedisTemplate.opsForValue().set(AUTH_LABEL_KEY, "", 1L, TimeUnit.MINUTES);
    }
    /**
     * 生成查询权限条件构造器
     * @param authManageDTO 查询权限条件
     * @return QueryWrapper<Auth>
     */
    private QueryWrapper<Auth> selectUser(AuthManageDTO authManageDTO) {
        QueryWrapper<Auth> authQueryWrapper = new QueryWrapper<>();
        // 判断条件是否为空
        if (authManageDTO == null) {
            return authQueryWrapper;
        }
        // 获取权限id
        Long authID = authManageDTO.getId();
        // 判断权限id是否不为空
        if (authID != null) {
            // 添加权限id条件
            authQueryWrapper.eq("id", authID);
        }

        // 获取权限名称
        String name = authManageDTO.getName();
        // 判断权限名称是否不为空
        if (StrUtil.isNotBlank(name)) {
            // 添加权限名称条件
            authQueryWrapper.eq("name", name);
        }

        // 获取权限标识
        String perms = authManageDTO.getPerms();
        // 判断权限标识是否不为空
        if (StrUtil.isNotBlank(perms)) {
            // 添加权限标识条件
            authQueryWrapper.eq("perms", perms);
        }

        // 获取权限状态
        Integer status = authManageDTO.getStatus();
        // 判断权限状态是否不为空
        if (status != null) {
            // 添加权限状态条件
            if (status == 0 || status == 1) {
                authQueryWrapper.eq("status", status);
            }
        }

        // 获取权限类型
        Integer type = authManageDTO.getType();
        // 判断权限类型是否不为空
        if (type != null) {
            // 添加权限类型条件
            if (type == 0 || type == 1 || type == 2 || type == 3) {
                authQueryWrapper.eq("type", type);
            }
        }

        // 获取权限标签
        String label = authManageDTO.getLabel();
        // 判断权限标签是否不为空
        if (StrUtil.isNotBlank(label)) {
            authQueryWrapper.eq("label", label);
        }
        return authQueryWrapper;
    }
}
