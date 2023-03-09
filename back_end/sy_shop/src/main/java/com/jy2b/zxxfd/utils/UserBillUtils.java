package com.jy2b.zxxfd.utils;

import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.UserBill;
import com.jy2b.zxxfd.domain.vo.BillStatusCode;
import com.jy2b.zxxfd.domain.vo.BillType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.jy2b.zxxfd.contants.RedisConstants.USER_BILL_KEY;

/**
 * @author 林武泰
 * 用户账单工具类
 */
@Component
public class UserBillUtils {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询用户当月账单
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return List<Bill>
     */
    public List<UserBill> queryUserBill(Long userId, int year, int month) {
        // 获取账单Key
        String cacheKey = USER_BILL_KEY + userId + ":" + year + ":" + (month < 10 ? "0" + month : month);
        // 查询该月所有账单
        Map<Object, Object> files = stringRedisTemplate.opsForHash().entries(cacheKey);
        Set<Object> keySet = files.keySet();
        List<UserBill> userBills = new ArrayList<>();
        for (Object key : keySet) {
            Object value = files.get(key);
            UserBill userBill = JSONUtil.toBean(value.toString(), UserBill.class);
            userBills.add(userBill);
        }
        return userBills;
    }

    /**
     * 保存用户账单
     * @param userId 用户ID
     * @param amount 交易金额
     * @param name 交易名称
     * @param merchantName 交易对象
     * @param billType 账单类型
     * @param payMethod 支付类型
     * @param remark 支付备注
     */
    public UserBill saveUserBill(Long userId, Double amount, String monetaryUnit, String name, String merchantName, BillType billType, String payMethod, BillStatusCode statusCode, String remark) {
        // 生成账单
        UserBill userBill = createBill(amount, monetaryUnit, name, merchantName, billType, payMethod, statusCode, remark);
        // 获取时间
        LocalDateTime time = userBill.getTime();
        // 获取年月
        String date = time.format(DateTimeFormatter.ofPattern("yyyy:MM"));
        // 生成账单Key
        String cacheKey = USER_BILL_KEY + userId + ":" + date;

        // 保存账单
        stringRedisTemplate.opsForHash().put(cacheKey, userBill.getId(), JSONUtil.toJsonStr(userBill));

        return userBill;
    }

    /**
     * 生成账单
     * @param amount 交易金额
     * @param monetaryUnit 货币单位
     * @param name 交易名称
     * @param merchantName 交易对象
     * @param billType 账单类型
     * @param payMethod 支付类型
     * @param statusCode 账单状态
     * @param remark 支付备注
     * @return Bill
     */
    public UserBill createBill(Double amount, String monetaryUnit, String name, String merchantName, BillType billType, String payMethod, BillStatusCode statusCode, String remark) {
        // 获取当前时间
        LocalDateTime nowDateTime = TimeUtils.nowLocalDateTime();
        // 获得年月日
        int year = nowDateTime.getYear();
        int month = nowDateTime.getMonthValue();
        int day = nowDateTime.getDayOfMonth();
        // 获取当前时间戳
        long timeMillis = System.currentTimeMillis();
        // 生成交易单号 16进制：200988e = 8进制：200114216
        String id = "200988e" + year + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day) + timeMillis;

        UserBill userBill = new UserBill();
        userBill.setId(id); // 设置交易单号
        userBill.setAmount(amount);  // 设置交易金额
        userBill.setMonetaryUnit(monetaryUnit); // 设置货币单位
        userBill.setName(name); // 设置交易商品
        userBill.setMerchantName(merchantName); // 设置商户名称
        userBill.setType(billType.getName()); // 设置账单类型
        userBill.setPayMethod(payMethod); // 设置支付方式
        userBill.setStatus(statusCode.getName()); // 设置订单状态
        userBill.setTime(nowDateTime); // 设置支付时间
        userBill.setRemark(remark); // 设置订单说明

        return userBill;
    }


}
