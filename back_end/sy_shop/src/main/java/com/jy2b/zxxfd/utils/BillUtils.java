package com.jy2b.zxxfd.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.dto.BillDTO;
import com.jy2b.zxxfd.contants.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BillUtils {
    private static StringRedisTemplate stringRedisTemplate;

    public BillUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 新增账单
     * @param userId 用户id
     * @param type 账单类型
     * @param amount 金额
     */
    public void saveBill(Long userId, String type, String amount) {
        // 获取日期
        LocalDateTime now = LocalDateTime.now();

        // 获取年月日
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        // 账单key prefix:id:year:month:day
        String billKey = RedisConstants.BILL_KEY + userId + ":" + date;

        // 账单记录时间
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        // 记录时间、类型、金额 hash的value
        String data = JSONUtil.toJsonStr(new BillDTO(time, type, amount));

        // 新增账单记录
        stringRedisTemplate.opsForHash().put(billKey, time, data);
    }

    /**
     * 获取所有账单
     * @param userId 用户id
     * @return 账单信息
     */
    public HashMap<String, ArrayList<BillDTO>> queryBill(Long userId) {
        // 账单key前缀
        String billKey = RedisConstants.BILL_KEY + userId;
        // 获取用户账单key集合
        Set<String> keys = stringRedisTemplate.keys(billKey + ":*");

        if (keys == null || keys.isEmpty()) {
            return null;
        }

        // 返回账单
        return foreachBill(keys);
    }

    /**
     * 获取账单
     * @param userId 用户id
     * @param year 年份
     * @param month 月份
     * @param day 日数
     * @return HashMap<String, ArrayList<BillDTO>>
     */
    public HashMap<String, ArrayList<BillDTO>> queryBillByDate(Long userId, String year, String month, String day) {
        // 获取key前缀
        String prefixKey = RedisConstants.BILL_KEY + userId;

        String isKey = prefixKey + ":*";
        // 判断年份是否不为空
        if (StrUtil.isNotBlank(year)) {
            isKey = prefixKey + ":" + year + ":*";
        }
        // 判断月份是否不为空
        if (StrUtil.isNotBlank(year) && StrUtil.isNotBlank(month)) {
            isKey = prefixKey + ":" + year + ":" + month + ":*";
        }
        // 判断期号是否不为空
        if (StrUtil.isNotBlank(year) && StrUtil.isNotBlank(month) && StrUtil.isNotBlank(day)) {
            isKey = prefixKey + ":" + year + ":" + month + ":" + day;
        }

        // 获取用户账单key集合
        Set<String> keys = stringRedisTemplate.keys(isKey);

        if (keys == null || keys.isEmpty()) {
            return null;
        }

        // 返回账单
        return foreachBill(keys);
    }

    /**
     * 获取账单
      * @param keys 账单key集合
     * @return HashMap<String, ArrayList<BillDTO>>
     */
    private HashMap<String, ArrayList<BillDTO>> foreachBill(Set<String> keys) {
        // key：日期  value：值
        HashMap<String, ArrayList<BillDTO>> listHashMap = new HashMap<>();
        // 查看每一日的账单
        for (String key : keys) {
            // 获取每一日的账单列表
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
            // 获取map中的key的集合
            Set<Object> keySet = map.keySet();

            ArrayList<String> sortKey = sort(keySet);

            ArrayList<BillDTO> arrayList = new ArrayList<>();

            // 循环每一个key
            for (String k : sortKey) {
                // 获得每一笔账单
                String value = (String) map.get(k);
                BillDTO billDTO = JSONUtil.toBean(value, BillDTO.class);
                // 将每一笔账单存入list中
                arrayList.add(billDTO);
            }

            String[] split = key.split(":");
            String time = split[2] + "年" + split[3] + "月" + split[4] + "日";
            listHashMap.put(time, arrayList);
        }

        return listHashMap;
    }

    /**
     * 账单时间排序
     * @param keys 账单key集合
     * @return ArrayList<String>
     */
    private ArrayList<String> sort(Set<Object> keys) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Object key : keys) {
            arrayList.add(String.valueOf(key));
        }

        ArrayList<Long> afterList = new ArrayList<>();
        for (String s : arrayList) {
            afterList.add(Long.valueOf(formatDate(s)));
        }

        afterList.sort((o1, o2) -> Math.toIntExact(o2 - o1));

        ArrayList<String> resultList = new ArrayList<>();
        for (Long aLong : afterList) {
            for (String s : arrayList) {
                Long aLong1 = Long.valueOf(formatDate(s));
                if (aLong.equals(aLong1)) {
                    resultList.add(s);
                }
            }
        }

        return resultList;
    }

    /**
     * 日期时间格式化
     * @param date 日期时间
     * @return String
     */
    private String formatDate(String date) {
        String[] s1 = date.split(" ");
        StringBuilder r1 = new StringBuilder();
        for (String s : s1) {
            r1.append(s);
        }

        String[] s2 = r1.toString().split("-");
        StringBuilder r2 = new StringBuilder();
        for (String s : s2) {
            r2.append(s);
        }

        String[] s3 = r2.toString().split(":");
        StringBuilder result = new StringBuilder();
        for (String s : s3) {
            result.append(s);
        }
        return result.toString();
    }
}
