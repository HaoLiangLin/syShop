package com.jy2b.zxxfd.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.dto.UserDTO;
import com.jy2b.zxxfd.domain.LoginUser;
import com.jy2b.zxxfd.utils.JwtUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jy2b.zxxfd.contants.RedisConstants.*;
import static com.jy2b.zxxfd.contants.SystemConstants.*;

/**
 * 认证过滤器（是否登录）
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的token
        String token = request.getHeader("authorization");

        if (StrUtil.isBlank(token)) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 解析token
        Claims claims = JwtUtils.parseJWT(token);
        if (claims == null) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 获取jwt密钥发行方
        if (!claims.getIssuer().equals(JWT_ISSUER)) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 获取jwt密钥主题
        if (!claims.getSubject().equals(JWT_SUBJECT)) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 获取jwt受众群体
        if (!claims.getAudience().equals(JWT_AUDIENCE)) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }

        // 获取key
        String key = LOGIN_USER_KEY + token;
        // 从Redis查询数据
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);

        // 数据存在
        if (!userMap.isEmpty()) {
            // 将map转换成Bean
            UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

            // 将用户信息保存
            UserHolder.saveUser(userDTO);

            // 刷新token有效期
            stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        }

        // 获取用户信息
        UserDTO user = UserHolder.getUser();

        // 用户信息为空，未登录
        if (user == null) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }

        // 从redis中获取系统用户相关信息
        String result = stringRedisTemplate.opsForValue().get("login:" + user.getId());

        // 系统用户相关信息不存在
        if (StrUtil.isBlank(result)) {
            // 删除用户信息
            stringRedisTemplate.delete(key);
            // 放行
            filterChain.doFilter(request, response);
            return;
        }

        // 将json格式转为bean
        LoginUser loginUser = JSONUtil.toBean(result, LoginUser.class);

        // 封装authentication
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

        // 存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        // 刷新系统用户相关信息有效期
        stringRedisTemplate.expire(LOGIN_KEY + user.getId(), LOGIN_KEY_TTL, TimeUnit.MINUTES);

        // 放行，让后面的过滤器执行
        filterChain.doFilter(request, response);
    }
}
