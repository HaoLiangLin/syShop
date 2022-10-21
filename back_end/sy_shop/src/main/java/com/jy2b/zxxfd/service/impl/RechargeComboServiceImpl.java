package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.RechargeComboSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.RechargeCombo;
import com.jy2b.zxxfd.mapper.RechargeComboMapper;
import com.jy2b.zxxfd.service.IRechargeComboService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RechargeComboServiceImpl extends ServiceImpl<RechargeComboMapper, RechargeCombo> implements IRechargeComboService {
    @Override
    public ResultVo saveCombo(RechargeComboSaveDTO comboSaveDTO) {
        // 获取套餐名称
        String name = comboSaveDTO.getName();
        // 查询套餐
        Integer nameCount = query().eq("name", name).count();
        // 判断套餐是否存在
        if (nameCount > 0) {
            return ResultVo.fail("新增充值套餐失败！充值套餐已存在！");
        }
        // 判断价格是否为空
        Double price = comboSaveDTO.getPrice();
        if (price == null || price <= 0) {
            return ResultVo.fail("价格不能小于0！");
        }
        // 判断积分是否为空
        Long points = comboSaveDTO.getPoints();
        if (points == null || points < 0) {
            return ResultVo.fail("赠送积分不能小于0！");
        }

        RechargeCombo rechargeCombo = new RechargeCombo();
        rechargeCombo.setName(name);
        rechargeCombo.setPrice(price);
        rechargeCombo.setPoints(points);

        boolean result = save(rechargeCombo);
        return result ? ResultVo.ok("新增充值套餐成功！") : ResultVo.fail("新增充值套餐失败！");
    }

    @Override
    public ResultVo delCombo(Long id) {
        // 查询充值套餐
        RechargeCombo combo = getById(id);
        // 判断套餐是否存在
        if (combo == null) {
            return ResultVo.fail("套餐不存在！");
        }
        boolean result = removeById(id);
        return result ? ResultVo.ok("删除充值套餐成功！") : ResultVo.fail("删除充值套餐失败！");
    }

    @Override
    public ResultVo updateCombo(RechargeCombo rechargeCombo) {
        // 查询要修改的充值套餐
        RechargeCombo beforeCombo = getById(rechargeCombo.getId());
        // 判断套餐是否存在
        if (beforeCombo == null) {
            return ResultVo.fail("套餐不存在！");
        }
        // 判断价格是否为空
        if (rechargeCombo.getPrice() == null || rechargeCombo.getPrice() <= 0) {
            return ResultVo.fail("价格不能小于等于0！");
        }
        // 判断积分是否为空
        if (rechargeCombo.getPoints() == null || rechargeCombo.getPoints() < 0) {
            return ResultVo.fail("赠送积分不能小于0！");
        }
        // 修改充值套餐
        boolean result = updateById(rechargeCombo);
        return result ? ResultVo.ok("修改充值套餐成功！") : ResultVo.fail("修改充值套餐失败！");
    }

    @Override
    public ResultVo queryCombo() {
        List<RechargeCombo> comboList = list();
        return ResultVo.ok(comboList);
    }

}
