package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.RechargeCombo;
import com.jy2b.zxxfd.domain.UserAccount;
import com.jy2b.zxxfd.mapper.AccountMapper;
import com.jy2b.zxxfd.mapper.RechargeComboMapper;
import com.jy2b.zxxfd.service.IAccountService;
import com.jy2b.zxxfd.utils.BillUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, UserAccount> implements IAccountService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RechargeComboMapper rechargeComboMapper;

    @Override
    public ResultVo queryAccount() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询用户钱包
        UserAccount account = getById(userId);

        // 判断是否为空
        if (account == null) {
            UserAccount userAccount = new UserAccount();
            userAccount.setId(userId);
            // 新增用户钱包
            save(userAccount);
            account = userAccount;
        }
        return ResultVo.ok(account);
    }

    @Override
    public ResultVo recharge(RechargeFromDTO rechargeFromDTO) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        if (rechargeFromDTO.getRechargeComboId() != null) {
            return rechargeCombo(userId, rechargeFromDTO.getRechargeComboId());
        }

        // 获取充值金额
        Double recharge = rechargeFromDTO.getRecharge();
        // 判断是否不为空
        if (recharge == null || recharge <= 0) {
            return ResultVo.fail("充值金额不能为空");
        }

        // 获取钱包信息
        UserAccount account = getById(userId);

        // 钱包充值
        boolean result = update()
                .set("balance", account.getBalance() + recharge)
                .set("recharge", account.getRecharge() + recharge)
                .eq("id", userId).update();


        if (result) {
            BillUtils billUtils = new BillUtils(stringRedisTemplate);
            // 设置账单类型
            String type = BillType.Recharge.getName();
            // 设置账单金额
            String amount = String.valueOf(recharge);
            // 新增账单
            billUtils.saveBill(userId, type, amount);

            return ResultVo.ok(null,"充值成功！充值金额：" + recharge);
        }


        return ResultVo.fail("充值失败");
    }

    /**
     * 充值套餐充值
     * @param userId 用户id
     * @param comboId 充值套餐id
     * @return ResultVo
     */
    private ResultVo rechargeCombo(Long userId, Long comboId) {
        // 获取套餐信息
        RechargeCombo combo = rechargeComboMapper.selectById(comboId);
        if (combo == null) {
            return ResultVo.fail("套餐不存在");
        }

        // 获取钱包信息
        UserAccount account = getById(userId);

        // 钱包充值
        boolean result = update()
                .set("balance", account.getBalance() + combo.getPrice())
                .set("recharge", account.getRecharge() + combo.getPrice())
                .set("points", account.getPoints() + combo.getPoints())
                .eq("id", userId).update();

        // 充值成功
        if (result) {
            BillUtils billUtils = new BillUtils(stringRedisTemplate);
            // 设置账单金额
            String amount = String.valueOf(combo.getPrice());
            // 新增充值账单
            billUtils.saveBill(userId, combo.getName(), amount);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 设置账单金额 积分
            String points = String.valueOf(combo.getPoints());
            // 新增充值赠送积分账单
            billUtils.saveBill(userId, combo.getName(), points + "积分");

            return ResultVo.ok(null,"充值成功！充值金额：" + combo.getPrice());
        }

        return ResultVo.fail("充值失败");
    }

    @Override
    public ResultVo queryBill(BillDateDTO dateDTO) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 获取账单
        BillUtils billUtils = new BillUtils(stringRedisTemplate);
        if (dateDTO != null) {
            ArrayList<BillVO> billVOS = billUtils.queryBillByDate(userId, dateDTO.getYear(), dateDTO.getMonth(), dateDTO.getDay());
            return ResultVo.ok(billVOS);
        }
        ArrayList<BillVO> billVOS = billUtils.queryBill(userId);
        return ResultVo.ok(billVOS);
    }
}
