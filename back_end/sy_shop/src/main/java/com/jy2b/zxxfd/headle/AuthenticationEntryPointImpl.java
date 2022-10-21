package com.jy2b.zxxfd.headle;

import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.dto.ResultVo;
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
        ResultVo resultVo = ResultVo.fail(authException.getMessage());

        WebUtils.renderString(response, JSONUtil.toJsonStr(resultVo));

        authException.printStackTrace();
    }
}
