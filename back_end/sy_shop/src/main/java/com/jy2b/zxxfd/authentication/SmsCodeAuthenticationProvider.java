package com.jy2b.zxxfd.authentication;

import com.jy2b.zxxfd.contants.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 手机验证码认证器
 */
@Component
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    @Resource
    private UserDetailsService smsDetailsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        // 获取手机号
        String phone = authenticationToken.getName();

        // 获取验证码
        String code = authenticationToken.getCredentials().toString();

        // 校验验证码
        checkSmsCode(phone, code);

        UserDetails userDetails = smsDetailsService.loadUserByUsername(authenticationToken.getName());

        // 返回经过认证的authentication
        SmsCodeAuthenticationToken result =
                new SmsCodeAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        result.setDetails(authentication.getDetails());

        return result;
    }

    private void checkSmsCode(String phone, String code) {
        String key = RedisConstants.LOGIN_CODE_KEY + phone;
        // 从redis获取验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(key);

        if (cacheCode == null) {
            throw new BadCredentialsException("验证码不存在！");
        }

        if (!code.equals(cacheCode)) {
            throw new BadCredentialsException("验证码错误！");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 SmsCodeAuthenticationToken 的子类或子接口
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
