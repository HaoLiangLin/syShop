package com.jy2b.zxxfd;

import com.jy2b.zxxfd.utils.JwtUtils;
import com.jy2b.zxxfd.utils.RedisIdWorker;
import com.jy2b.zxxfd.utils.RegexUtils;
import com.jy2b.zxxfd.utils.TimeUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import static com.jy2b.zxxfd.contants.RedisConstants.ORDER_KEY;

@SpringBootTest
class SyShopApplicationTests {

    @Test
    void contextLoads() {
        boolean usernameInvalid = RegexUtils.isUsernameInvalid("a13071510830");
        System.out.println("usernameInvalid = " + usernameInvalid);
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test1() {
        RedisIdWorker redisIdWorker = new RedisIdWorker(stringRedisTemplate);
        long order = redisIdWorker.nextId(ORDER_KEY);
        System.out.println("order = " + order);
    }

    @Test
    void jwtTest() {
        String jwt = JwtUtils.generateJwt(10010l);
        System.out.println("jwt = " + jwt);
        Claims claims = JwtUtils.parseJWT(jwt);
        System.out.println("claims = " + claims.toString());
    }

    @Test
    void test2() {
        String s = "apple";

        String[] split = s.split(",");
        for (String s1 : split) {
            System.out.println("s1 = " + s1);
        }
    }

    @Test
    void test3() {
        String s = "bill:1577368407370752002:2022:10:16";

        String[] split = s.split(":");
        String time = split[2] + split[3] +split[4];

        System.out.println("time = " + time);
    }

    @Test
    void test4() {
        String property = System.getProperty("user.dir");
        System.out.println("property = " + property);

        String path = Objects.requireNonNull(this.getClass().getResource("")).getPath();
        System.out.println("path = " + path);
    }

    @Test
    void test5() {
        Date date = new Date();
        String time = TimeUtils.dateToStringTime(date);
        System.out.println("time = " + time);
    }
}
