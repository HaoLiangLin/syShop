package com.jy2b.zxxfd;

import com.jy2b.zxxfd.domain.dto.BillVO;
import com.jy2b.zxxfd.utils.BillUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
public class IAccountServiceTest {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        BillUtils billUtils = new BillUtils(stringRedisTemplate);
        ArrayList<BillVO> bill = billUtils.queryBill(1577368407370752002l);
        System.out.println(bill);
    }

    @Test
    void test1() {
        // 将字符串转为数字
        String key1 = "2022-10-17 00:39:28:117";
        String[] s1 = key1.split(" ");
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
        System.out.println(result);
    }

    @Test
    void test2() {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(5);
        integers.add(2);
        integers.add(4);
        System.out.println(integers);

        Integer one = integers.get(0);

        LinkedList<Integer> linkedList = new LinkedList<>();
    }
}
