package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.RechargeComboManageDTO;
import com.jy2b.zxxfd.domain.RechargeCombo;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.RechargeComboMapper;
import com.jy2b.zxxfd.service.IRechargeComboService;
import com.jy2b.zxxfd.utils.RedisUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jy2b.zxxfd.contants.RedisConstants.RECHARGE_COMBO_KEY;

@Service
public class RechargeComboServiceImpl extends ServiceImpl<RechargeComboMapper, RechargeCombo> implements IRechargeComboService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public List<RechargeCombo> selectAllRechargeCombo() {
        // 查询缓存
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RECHARGE_COMBO_KEY);
        // 判断是否不为空
        if (!entries.isEmpty()) {
            // 获取所有值
            return entries.values().stream().map(r -> JSONUtil.toBean(r.toString(), RechargeCombo.class)).collect(Collectors.toList());
        }

        // 查询全部充值套餐
        List<RechargeCombo> rechargeComboList = list();
        if (rechargeComboList.isEmpty()) {
            return rechargeComboList;
        }
        // 保存缓存
        for (RechargeCombo rechargeCombo : rechargeComboList) {
            stringRedisTemplate.opsForHash().put(RECHARGE_COMBO_KEY, rechargeCombo.getId().toString(), JSONUtil.toJsonStr(rechargeCombo));
        }
        return rechargeComboList;
    }

    @Override
    public RechargeCombo selectRechargeComboById(Long rechargeComboId) {
        // 根据ID查询充值套餐
        Object result = stringRedisTemplate.opsForHash().get(RECHARGE_COMBO_KEY, rechargeComboId.toString());
        // 判断是否为空
        if (result != null) {
            return JSONUtil.toBean(result.toString(), RechargeCombo.class);
        }

        return null;
    }

    @Override
    public ResultVO updateRechargeCombo(Long rechargeComboId, RechargeComboManageDTO rechargeComboManageDTO) {
        // 获取充值套餐
        Object result = stringRedisTemplate.opsForHash().get(RECHARGE_COMBO_KEY, rechargeComboId.toString());
        // 判断是否存在
        if (result == null) {
            return ResultVO.fail("充值套餐不存在");
        }

        UpdateWrapper<RechargeCombo> comboUpdateWrapper = new UpdateWrapper<>();
        // 获取套餐名称
        String name = rechargeComboManageDTO.getName();
        // 判断是否不为空
        if (StrUtil.isNotBlank(name)) {
            Integer count = query().eq("name", name).count();
            if (count > 0) {
                return ResultVO.fail("套餐名称已存在");
            }
            comboUpdateWrapper.set("name", name);
        }

        // 获取充值金额
        Double price = rechargeComboManageDTO.getPrice();
        // 判断是否不为空
        if (price != null) {
            if (price < 0) {
                return ResultVO.fail("价格不能小于零");
            }
            comboUpdateWrapper.set("price", price);
        }

        // 获取套餐赠送积分
        Integer points = rechargeComboManageDTO.getPoints();
        // 判断是否不为空
        if (points != null) {
            if (points < 0) {
                return ResultVO.fail("赠送积分不能小于零");
            }
            comboUpdateWrapper.set("points", points);
        }

        // 获取套餐折扣
        Double discount = rechargeComboManageDTO.getDiscount();
        if (discount != null) {
            if (discount < 0 || discount > 1) {
                return ResultVO.fail("折扣取值为0-1之间");
            }
            comboUpdateWrapper.set("discount", discount);
        }

        comboUpdateWrapper.eq("id", rechargeComboId);

        boolean updateResult = update(comboUpdateWrapper);
        if (updateResult) {
            RechargeCombo rechargeCombo = getById(rechargeComboId);
            // 更新缓存
            updateCache(rechargeCombo.getId(), rechargeCombo);
        }
        return updateResult ? ResultVO.ok("修改成功") : ResultVO.fail("修改失败");
    }

    @Override
    public ResultVO saveRechargeCombo(RechargeComboManageDTO rechargeComboManageDTO) {
        // 获取套餐名称
        String name = rechargeComboManageDTO.getName();
        // 判断套餐名称是否为空
        if (StrUtil.isBlank(name)) {
            return ResultVO.fail("套餐名称不能为空");
        }
        // 获取套餐价格
        Double price = rechargeComboManageDTO.getPrice();
        // 判断套餐价格是否为空
        if (price == null) {
            return ResultVO.fail("套餐价格不能为空");
        }
        if (price < 0) {
            return ResultVO.fail("套餐价格不得小于0");
        }
        // 获取套餐赠送积分
        Integer points = rechargeComboManageDTO.getPoints();
        // 判断套餐积分是否不为空
        if (points != null) {
            if (points < 0) {
                return ResultVO.fail("赠送积分不能小于零");
            }
        } else {
            points = 0;
        }
        // 获取套餐折扣
        Double discount = rechargeComboManageDTO.getDiscount();
        // 判断折扣是否不为空
        if (discount != null) {
            if (discount < 0 || discount > 1) {
                return ResultVO.fail("折扣取值为0-1之间");
            }
        } else {
            discount = 1.00;
        }

        // 判断套餐名称是否重复
        Integer nameCount = query().eq("name", name).count();
        if (nameCount > 0) {
            return ResultVO.fail("套餐名称已存在");
        }

        // 新增充值套餐
        RechargeCombo rechargeCombo = new RechargeCombo();
        rechargeCombo.setName(name);
        rechargeCombo.setPrice(price);
        rechargeCombo.setPoints(points);
        rechargeCombo.setDiscount(discount);
        boolean saveResult = save(rechargeCombo);
        if (saveResult) {
            // 更新缓存
            updateCache(rechargeCombo.getId(), rechargeCombo);
        }
        return saveResult ? ResultVO.ok(rechargeCombo, "新增成功") : ResultVO.fail("新增失败");
    }

    @Override
    public ResultVO delRechargeCombo(Long rechargeComboId) {
        // 判断充值套餐是否存在
        Object result = stringRedisTemplate.opsForHash().get(RECHARGE_COMBO_KEY, rechargeComboId.toString());
        if (result == null) {
            return ResultVO.fail("充值套餐不存在");
        }
        // 资源回收
        RechargeCombo rechargeCombo = JSONUtil.toBean(result.toString(), RechargeCombo.class);

        // 删除充值套餐
        boolean removeResult = removeById(rechargeComboId);
        if (removeResult) {
            // 删除缓存
            stringRedisTemplate.opsForHash().delete(RECHARGE_COMBO_KEY, rechargeComboId.toString());

            redisUtils.setRecycleBin(rechargeCombo, rechargeCombo.getName(), null, null);
        }
        return removeResult ? ResultVO.ok(rechargeCombo, "删除成功") : ResultVO.fail("删除失败");
    }

    /**
     * 更新缓存
     * @param rechargeComboId 充值套餐ID
     * @param rechargeCombo 充值套餐
     */
    @Override
    public void updateCache(Long rechargeComboId, RechargeCombo rechargeCombo) {
        stringRedisTemplate.opsForHash().put(RECHARGE_COMBO_KEY, rechargeComboId.toString(), JSONUtil.toJsonStr(rechargeCombo));
    }

}
