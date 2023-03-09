package com.jy2b.zxxfd.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 林武泰
 * Web工具类
 */
public class WebUtils {
    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param value 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String value) {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try {
            response.getWriter().print(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
