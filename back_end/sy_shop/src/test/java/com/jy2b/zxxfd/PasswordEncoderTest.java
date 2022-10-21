package com.jy2b.zxxfd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest
public class PasswordEncoderTest {
    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    void test() {
        // 密码加密
        String password = passwordEncoder.encode("");
        System.out.println(password);

        // 密码校验
        boolean flag = passwordEncoder.matches("", password);
        System.out.println("flag = " + flag);
    }
}
