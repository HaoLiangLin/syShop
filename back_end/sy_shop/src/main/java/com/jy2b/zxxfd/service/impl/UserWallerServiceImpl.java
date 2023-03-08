package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.RechargeCombo;
import com.jy2b.zxxfd.domain.UserBill;
import com.jy2b.zxxfd.domain.UserWaller;
import com.jy2b.zxxfd.domain.dto.RechargeDTO;
import com.jy2b.zxxfd.domain.vo.BillStatusCode;
import com.jy2b.zxxfd.domain.vo.BillType;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.RechargeComboMapper;
import com.jy2b.zxxfd.mapper.UserWallerMapper;
import com.jy2b.zxxfd.service.IBillService;
import com.jy2b.zxxfd.service.IUserWallerService;
import com.jy2b.zxxfd.utils.RedisUtils;
import com.jy2b.zxxfd.utils.UserBillUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


import java.util.List;

import static com.jy2b.zxxfd.contants.RedisConstants.USER_WALLER_KEY;
import static com.jy2b.zxxfd.contants.SystemConstants.APPLICATION_NAME;

@Service
public class UserWallerServiceImpl extends ServiceImpl<UserWallerMapper, UserWaller> implements IUserWallerService {
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserBillUtils userBillUtils;

    @Resource
    private IBillService billService;

    @Resource
    private RechargeComboMapper rechargeComboMapper;

    @Override
    public ResultVO selectWaller() {
        // 获取用户ID
        Long userId = UserHolder.getUser().getId();
        // 查询用户钱包
        UserWaller userWaller = redisUtils.queryWithPassThrough(USER_WALLER_KEY, userId, UserWaller.class, this::getById, null, null);
        // 判断用户钱包是否不存在
        if (userWaller == null) {
            return ResultVO.fail("钱包不存在");
        }
        return ResultVO.ok(userWaller, "查询成功");
    }

    @Override
    public ResultVO rechargeWaller(RechargeDTO rechargeDTO) {
        // 获取充值类型
        Integer chargeType = rechargeDTO.getChargeType();

        // 获取用户ID
        Long userId = UserHolder.getUser().getId();
        // 查询用户钱包
        UserWaller userWaller = redisUtils.queryWithPassThrough(USER_WALLER_KEY, userId, UserWaller.class, this::getById, null, null);
        // 判断用户钱包是否不存在
        if (userWaller == null) {
            return ResultVO.fail("钱包不存在");
        }

        // 0：任意充值
        if (chargeType.equals(0)) {
            // 获取充值金额
            Double amount = rechargeDTO.getRechargeAmount();
            if (amount == null) {
                return ResultVO.fail("充值金额不能为空");
            }
            if (amount <= 0) {
                return ResultVO.fail("充值金额不能小于等于0");
            }
            String errMsg = userDefinedRecharge(userId, amount);
            if (StrUtil.isNotBlank(errMsg)) {
                return ResultVO.fail(errMsg);
            }
            return ResultVO.ok("充值成功");
        }
        // 1：套餐充值
        if (chargeType.equals(1)) {
            // 获取充值套餐id
            Long rechargeComboId = rechargeDTO.getRechargeComboId();
            if (rechargeComboId == null) {
                return ResultVO.fail("充值套餐ID不能为空");
            }
            String errMsg = rechargeCombo(userId, rechargeComboId);
            if (StrUtil.isNotBlank(errMsg)) {
                return ResultVO.fail(errMsg);
            }
            return ResultVO.ok("充值成功");
        }
        return ResultVO.fail("充值类型错误");
    }

    @Override
    public ResultVO selectUserBill(int year, int month) {
        // 判断年份是否正确
        if (year < 2022) {
            return ResultVO.fail("错误年份");
        }
        // 判断月份是否正确
        if (month > 12 || month < 0) {
            return ResultVO.fail("错误月份");
        }

        // 获取用户ID
        Long userId = UserHolder.getUser().getId();
        // 查询用户账单
        List<UserBill> userBills = userBillUtils.queryUserBill(userId, year, month);
        return ResultVO.ok(userBills, "查询成功");
    }

    /**
     * 用户自定义充值
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 错误信息，null则成功
     */
    private String userDefinedRecharge(Long userId, Double amount) {
        // 查询用户钱包
        UserWaller userWaller = getById(userId);
        UpdateWrapper<UserWaller> wallerUpdateWrapper = new UpdateWrapper<>();
        wallerUpdateWrapper.set("balance", userWaller.getBalance() + amount);
        wallerUpdateWrapper.set("recharge", userWaller.getRecharge() + amount);
        wallerUpdateWrapper.eq("id", userId);
        // 钱包充值
        boolean updateResult = update(wallerUpdateWrapper);
        if (updateResult) {
            // 保存用户账单
            UserBill userBill = userBillUtils.saveUserBill(userId, amount, null, "钱包充值", APPLICATION_NAME, BillType.income, null, BillStatusCode.rechargeSuccess, "");
            // 保存系统账单
            billService.saveBill(userId, userBill, BillType.income);
            // 更新缓存
            UserWaller waller = getById(userId);
            redisUtils.setStr(USER_WALLER_KEY + ":" + userId, JSONUtil.toJsonStr(waller), null, null);
            // 充值成功
            return null;
        }

        return  "充值失败";
    }

    /**
     * 充值套餐充值
     * @param userId 用户ID
     * @param rechargeComboId 充值套餐ID
     * @return 错误信息，null则成功
     */
    private String rechargeCombo(Long userId, Long rechargeComboId) {
        // 查询用户钱包
        UserWaller userWaller = getById(userId);
        // 查询充值套餐
        RechargeCombo rechargeCombo = rechargeComboMapper.selectById(rechargeComboId);
        if (rechargeCombo == null) {
            return "充值套餐不存在";
        }
        // 获取充值金额
        Double price = rechargeCombo.getPrice();
        // 获取赠送积分
        Integer points = rechargeCombo.getPoints();

        UpdateWrapper<UserWaller> wallerUpdateWrapper = new UpdateWrapper<>();
        wallerUpdateWrapper.set("balance", userWaller.getBalance() + price);
        wallerUpdateWrapper.set("recharge", userWaller.getRecharge() + price);
        wallerUpdateWrapper.set("points", userWaller.getPoints() + points);
        wallerUpdateWrapper.eq("id", userId);
        // 钱包充值
        boolean updateResult = update(wallerUpdateWrapper);
        if (updateResult) {
            // 保存用户账单
            UserBill userBill = userBillUtils.saveUserBill(userId, price, null, "钱包充值", APPLICATION_NAME, BillType.income, null, BillStatusCode.rechargeSuccess, "");
            // 保存用户积分账单
            userBillUtils.saveUserBill(userId, Double.valueOf(points), "积分", "钱包充值", APPLICATION_NAME, BillType.income, null, BillStatusCode.rechargeSuccess, "");
            // 保存系统账单
            billService.saveBill(userId, userBill, BillType.income);
            // 更新缓存
            UserWaller waller = getById(userId);
            redisUtils.setStr(USER_WALLER_KEY + ":" + userId, JSONUtil.toJsonStr(waller), null, null);
            return null;
        }
        return "充值失败";
    }
}
