package com.jy2b.zxxfd;


import com.jy2b.zxxfd.service.IUserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserInfoServiceTest {
    @Resource
    private IUserInfoService userInfoService;

    @Test
    void pageTest() {

    }
}
