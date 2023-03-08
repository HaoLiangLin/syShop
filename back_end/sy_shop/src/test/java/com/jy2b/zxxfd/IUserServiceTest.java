package com.jy2b.zxxfd;

import com.jy2b.zxxfd.domain.User;
import com.jy2b.zxxfd.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class IUserServiceTest {
    @Resource
    private IUserService userService;

    @Test
    void saveTest() {
        User user = new User();
        user.setPhone("13071510830");
        user.setUsername("admin");
        user.setPassword("admin");
        user.setNickname("最高管理员");
        user.setUserType(0);
        userService.save(user);
    }

    @Test
    void timeTest() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        System.out.println("format = " + format);
    }
}
