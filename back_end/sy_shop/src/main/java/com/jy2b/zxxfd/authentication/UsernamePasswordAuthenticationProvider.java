package com.jy2b.zxxfd.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author 林武泰
 * 账号密码认证器
 */
@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    @Resource
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取用户名
        String username = authentication.getName();

        // 获取密码
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (Objects.isNull(userDetails) || !new BCryptPasswordEncoder().matches(password, userDetails.getPassword())) {
            throw new UsernameNotFoundException("账号或密码错误！");
        }

        // 返回认证的authentication
        UsernamePasswordAuthenticationToken result =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
