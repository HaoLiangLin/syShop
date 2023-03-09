package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.UserWaller;
import com.jy2b.zxxfd.domain.dto.RechargeDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 用户钱包业务接口
 */
@Transactional
public interface IUserWallerService extends IService<UserWaller> {
    /**
     * 查询钱包
     * @return ResultVO
     */
    ResultVO selectWaller();

    /**
     * 钱包充值
     * @param rechargeDTO 充值积分
     * @return ResultVO
     */
    ResultVO rechargeWaller(RechargeDTO rechargeDTO);

    /**
     * 查询用户账单
     * @param year 年份
     * @param month 月份
     * @return ResultVO
     */
    ResultVO selectUserBill(int year, int month);
}
