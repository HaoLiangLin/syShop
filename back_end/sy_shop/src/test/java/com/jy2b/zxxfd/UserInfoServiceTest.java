package com.jy2b.zxxfd;


import com.jy2b.zxxfd.service.IUserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class UserInfoServiceTest {
    @Resource
    private IUserInfoService userInfoService;

    @Test
    void pageTest() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        System.out.println("date = " + date);
    }
}
