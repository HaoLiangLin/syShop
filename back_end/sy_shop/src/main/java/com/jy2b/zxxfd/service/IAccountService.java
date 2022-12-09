package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.RechargeFromDTO;
import com.jy2b.zxxfd.domain.dto.BillDateDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserAccount;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IAccountService extends IService<UserAccount> {
    /**
     * 查询钱包信息 简易
     * @return ResultVo
     */
    ResultVo queryAccount();

    /**
     * 钱包充值
     * @param rechargeFromDTO 充值信息
     * @return ResultVo
     */
    ResultVo recharge(RechargeFromDTO rechargeFromDTO);

    /**
     * 查询账单
     * @return ResultVo
     */
    ResultVo queryBill(BillDateDTO dateDTO);
}
