package com.jy2b.zxxfd;

import com.jy2b.zxxfd.mapper.UserDetailsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class UserDetailsMapperTest {
    @Resource
    UserDetailsMapper userDetailsMapper;

    @Test
    void test() {
        List<String> list = userDetailsMapper.queryAuthPermsByUserId(200114216L);
        System.out.println("list = " + list);
    }
}
