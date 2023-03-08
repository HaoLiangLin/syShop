package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Bill;
import com.jy2b.zxxfd.domain.UserBill;
import com.jy2b.zxxfd.domain.vo.BillType;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.BillMapper;
import com.jy2b.zxxfd.service.IBillService;
import com.jy2b.zxxfd.utils.TimeUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements IBillService {
    @Override
    public void saveBill(Long userId, UserBill userBill, BillType billType) {
        Bill bill = new Bill();
        bill.setUserId(userId);
        bill.setBillId(userBill.getId());
        bill.setName(userBill.getName());
        bill.setAmount(userBill.getAmount());
        bill.setType(billType.getName());
        bill.setTime(userBill.getTime());
        save(bill);
    }

    @Override
    public ResultVO billCount(Long startDate, Long endDate) {
        // 获取起始时间
        Date start = new Date(startDate);
        // 获取截止时间
        Date end = new Date(endDate);
        // 判断截止时间是否早于起始时间
        if (!start.equals(end)) {
            if (start.after(end)) {
                return ResultVO.fail("截止时间不得早于起始时间");
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateTime = simpleDateFormat.format(start);
        String endDateTime = simpleDateFormat.format(end);

        // 查询注册时间在指定区间内的账单
        List<Bill> billList = query().ge("time", startDateTime).le("time", endDateTime).orderByAsc("time").list();

        // 获取指定时间段中全部日期
        List<Date> dumDateList = TimeUtils.getDumDateList(startDate, endDate);

        List<Map<String, Object>> resultMap = new ArrayList<>();
        // 收入总额
        double incomeAll = 0;
        // 支出总额
        double disburseAll = 0;
        for (Date date : dumDateList) {
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, Object> listMap = new HashMap<>();
            Map<String, Object> childMap = new HashMap<>();

            String nowDate = simpleDate.format(date);
            // 获取当日全部账单
            List<Bill> bills = billList.stream().filter(bill -> {
                simpleDate.setTimeZone(TimeZone.getTimeZone("GTM+8"));
                String time = simpleDate.format(bill.getTime());
                return nowDate.equals(time);
            }).collect(Collectors.toList());

            // 获取当日收入账单
            List<Bill> incomeBill = bills.stream().filter(bill -> bill.getType().equals(BillType.income.getName())).collect(Collectors.toList());
            // 获取收入总额
            double income = 0;
            for (Bill bill : incomeBill) {
                income += bill.getAmount();
            }
            incomeAll += income;
            Map<String, Object> map1 = new HashMap<>();
            map1.put("income", income);
            map1.put("data", incomeBill);
            childMap.put("incomeBill", map1);

            // 获取当日支出账单
            List<Bill> disburseBill = bills.stream().filter(bill -> bill.getType().equals(BillType.disburse.getName())).collect(Collectors.toList());
            // 获取支出总额
            double disburse = 0;
            for (Bill bill : incomeBill) {
                disburse += bill.getAmount();
            }
            disburseAll += disburse;
            Map<String, Object> map2 = new HashMap<>();
            map1.put("disburse", disburse);
            map1.put("data", disburseBill);
            childMap.put("disburseBill", map2);

            listMap.put("time", nowDate);
            listMap.put("data", childMap);

            resultMap.add(listMap);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("incomeAll", incomeAll);
        map.put("disburseAll", disburseAll);

        resultMap.add(map);
        return ResultVO.ok(resultMap, "查询成功");
    }
}
