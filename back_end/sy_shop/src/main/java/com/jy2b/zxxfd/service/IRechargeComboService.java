package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.RechargeComboManageDTO;
import com.jy2b.zxxfd.domain.RechargeCombo;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IRechargeComboService extends IService<RechargeCombo> {
    /**
     * 查询全部充值套餐
     * @return List<RechargeCombo>
     */
    List<RechargeCombo> selectAllRechargeCombo();

    /**
     * 根据套餐id查询充值套餐
     * @param rechargeComboId 充值套餐id
     * @return RechargeCombo
     */
    RechargeCombo selectRechargeComboById(Long rechargeComboId);

    /**
     * 修改充值套餐
     * @param rechargeComboId 充值套餐Id
     * @param rechargeComboManageDTO 充值套餐信息
     * @return ResultVO
     */
    ResultVO updateRechargeCombo(Long rechargeComboId, RechargeComboManageDTO rechargeComboManageDTO);

    /**
     * 新增充值套餐
     * @param rechargeComboManageDTO 充值套餐信息
     * @return ResultVO
     */
    ResultVO saveRechargeCombo(RechargeComboManageDTO rechargeComboManageDTO);

    /**
     * 删除充值套餐
     * @param rechargeComboId 充值套餐id
     * @return ResultVO
     */
    ResultVO delRechargeCombo(Long rechargeComboId);

    /**
     * 更新缓存
     * @param rechargeComboId 充值套餐Id
     * @param rechargeCombo 充值套餐
     */
    void updateCache(Long rechargeComboId, RechargeCombo rechargeCombo);
}
