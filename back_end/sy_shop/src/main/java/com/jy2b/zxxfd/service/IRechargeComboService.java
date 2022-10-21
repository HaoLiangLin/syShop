package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.RechargeComboSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.RechargeCombo;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IRechargeComboService extends IService<RechargeCombo> {
    /**
     * 新增充值套餐
     * @param comboSaveDTO 套餐信息
     * @return ResultVo
     */
    ResultVo saveCombo(RechargeComboSaveDTO comboSaveDTO);

    /**
     * 删除充值套餐
     * @param id 套餐id
     * @return ResultVo
     */
    ResultVo delCombo(Long id);

    /**
     * 修改充值套餐
     * @param rechargeCombo 套餐信息
     * @return ResultVo
     */
    ResultVo updateCombo(RechargeCombo rechargeCombo);

    /**
     * 查询充值套餐
     * @return ResultVo
     */
    ResultVo queryCombo();
}
