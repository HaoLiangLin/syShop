package com.jy2b.zxxfd.headle;

import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.utils.WebUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandleImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResultVo resultVo = ResultVo.fail("您的权限不足，暂无法访问！");
        String result = JSONUtil.toJsonStr(resultVo);
        WebUtils.renderString(response, result);
    }
}
