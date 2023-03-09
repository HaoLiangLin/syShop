package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.Bill;
import com.jy2b.zxxfd.domain.UserBill;
import com.jy2b.zxxfd.domain.vo.BillType;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 账单业务接口
 */
@Transactional
public interface IBillService extends IService<Bill> {
    /**
     * 保存系统账单
     * @param userId 用户ID
     * @param userBill 用户账单
     */
    void saveBill(Long userId, UserBill userBill, BillType billType);

    /**
     * 账单统计
     * @param startDate 起始时间戳
     * @param endDate 截止时间戳
     * @return ResultVO
     */
    ResultVO billCount(Long startDate, Long endDate);
}
