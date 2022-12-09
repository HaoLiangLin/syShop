package com.jy2b.zxxfd.config;

import com.jy2b.zxxfd.authentication.MyAuthenticationFilter;
import com.jy2b.zxxfd.filter.JwtAuthenticationTokenFilter;
import com.jy2b.zxxfd.authentication.SmsCodeAuthenticationProvider;
import com.jy2b.zxxfd.authentication.UsernamePasswordAuthenticationProvider;
import com.jy2b.zxxfd.headle.AccessDeniedHandleImpl;
import com.jy2b.zxxfd.headle.AuthenticationEntryPointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
// 开启权限控制注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Resource
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Resource
    private AccessDeniedHandleImpl accessDeniedHandle;

    // 账号密码登录认证器
    @Resource
    private UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    // 手机验证码登录认证器
    @Resource
    private SmsCodeAuthenticationProvider smsCodeAuthenticationProvider;


    /**
     * 设置多个认证器，含有以上两个AuthenticationProvider的AuthenticationManager
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        ProviderManager providerManager =
                new ProviderManager(Arrays.asList(usernamePasswordAuthenticationProvider, smsCodeAuthenticationProvider));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    /**
     * 开启密码加密
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MyAuthenticationFilter myAuthenticationFilter() {
        MyAuthenticationFilter myAuthenticationFilter = new MyAuthenticationFilter();
        try {
            myAuthenticationFilter.setAuthenticationManager(authenticationManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myAuthenticationFilter;
    }

    /**
     * 配置spring security规则 AuthenticationConfigurer
     * @param http 形参
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // 关闭csrf
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 不通过session获取SecurityContext
            .and()
            .authorizeRequests()
            .antMatchers(
                    "/users/login",
                    "/users/loginEnd/**",
                    "/users/code",
                    "/users/register/**",
                    "/users/codePassword",
                    "/users/check/codePassword",
                    "/users/updatePassword").anonymous() // 只有未登录的才能访问
            .antMatchers(
                    "/goodsCategory/query/**",
                    "/goodsCategory/select",
                    "/goods/query/**",
                    "/goodsItem/query/**",
                    "/evaluation/query/**",
                    "/comments/query/**",
                    "/events/query/**",
                    "/eventsGoods/query/**",
                    "/noticeCategory/query/**",
                    "/noticeCategory/select",
                    "/notices/query/**",
                    "/resources/image/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/v2/**",
                    "/swagger-ui.html/**").permitAll() // 无论是否登录，都可以访问
            .anyRequest().authenticated(); // 除以上请求，其他都需要鉴权认证


        // 将认证过滤器放置在UsernamePasswordAuthenticationFilter过滤器之前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 添加认证器
        http.authenticationProvider(usernamePasswordAuthenticationProvider)
            .authenticationProvider(smsCodeAuthenticationProvider);

        http.authenticationManager(authenticationManager());

        // 处理异常
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandle);

        // 允许跨域
        http.cors();
        return http.build();
    }
}
