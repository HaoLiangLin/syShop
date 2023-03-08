package com.jy2b.zxxfd.service.impl;

import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IRecycleBinService;
import com.jy2b.zxxfd.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.function.Function;

@Service
public class RecycleBinServiceImpl implements IRecycleBinService {
    @Resource
    private RedisUtils redisUtils;

    @Override
    public <T> ResultVO selectRecycleBin(Class<T> type) {
        // 根据资源类型查询回收站资源
        Set<String> recycleBin = redisUtils.selectRecycleBin(type);
        return ResultVO.ok(recycleBin, "查询成功");
    }

    @Override
    public <T> ResultVO findRecycleBinByName(Class<T> type, String name) {
        // 根据资源类型与资源名称查询回收站资源
        T recycleBin = redisUtils.getRecycleBin(type, name);
        return recycleBin != null ? ResultVO.ok(recycleBin, "查询成功") : ResultVO.fail("资源不存在");
    }

    @Override
    public <T> ResultVO delRecycleBinByName(Class<T> type, String name) {
        // 根据资源类型与资源名称删除回收站资源
        Boolean result = redisUtils.removeRecycleBin(type, name);
        return result ? ResultVO.ok("删除成功") : ResultVO.fail("资源不存在");
    }

    @Override
    public <T> ResultVO regainRecycleBinByName(Class<T> type, String name, Function<T, Boolean> dbFall) {
        // 根据资源类型与资源名称获取资源
        T recycleBin = redisUtils.getRecycleBin(type, name);
        // 判断资源是否不为空
        if (recycleBin == null) {
            return ResultVO.fail("资源不存在");
        }
        // 恢复资源
        Boolean result = dbFall.apply(recycleBin);
        // 判断是否成功
        if (result) {
            // 删除回收站中资源
            redisUtils.removeRecycleBin(type, name);
        }
        return result ? ResultVO.ok(recycleBin, "恢复成功") : ResultVO.fail("恢复失败");
    }
}
