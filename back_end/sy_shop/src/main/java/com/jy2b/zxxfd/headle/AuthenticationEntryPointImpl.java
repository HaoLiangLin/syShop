package com.jy2b.zxxfd.headle;

import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.utils.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResultVO resultVo = ResultVO.fail(authException.getMessage());

        if (authException.getMessage().equals("Full authentication is required to access this resource")) {
            resultVo.setMessage("您的登录认证无效");
        }

        WebUtils.renderString(response, JSONUtil.toJsonStr(resultVo));
    }
}
