package com.jy2b.zxxfd.authentication;

import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.dto.LoginDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author 林武泰
 * 手机验证码登录过滤器
 */
public class MyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/login", "POST");
    private boolean postOnly = true;

    public MyAuthenticationFilter() {super(DEFAULT_ANT_PATH_REQUEST_MATCHER);}

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null){
                responseStrBuilder.append(inputStr);
            }

            LoginDTO loginDTO = JSONUtil.toBean(responseStrBuilder.toString(), LoginDTO.class);

            AbstractAuthenticationToken authenticationToken = null;

            // 获取登录类型
            Integer loginType = loginDTO.getLoginType();

            if (loginType != null && loginType == 0) {
                // 获取用户名
                String username = loginDTO.getUsername();
                // 获取密码
                String password = loginDTO.getPassword();
                // 账号密码登录
                authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            }

            if (loginType != null && loginType == 1) {
                // 获取手机号
                String phone = loginDTO.getPhone();
                // 获取验证码
                String code = loginDTO.getCode();
                // 手机号验证码登录
                authenticationToken = SmsCodeAuthenticationToken.unauthenticated(phone, code);
            }
            setDetails(request, authenticationToken);
            return this.getAuthenticationManager().authenticate(authenticationToken);
        }
    }

    protected void setDetails(HttpServletRequest request, AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
