package com.jy2b.zxxfd.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jy2b.zxxfd.domain.dto.UserDTO;
import com.jy2b.zxxfd.domain.LoginUser;
import com.jy2b.zxxfd.utils.JwtUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import eu.bitwalker.useragentutils.*;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jy2b.zxxfd.contants.RedisConstants.*;
import static com.jy2b.zxxfd.contants.SystemConstants.*;

/**
 * @author 林武泰
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
        // 获取请求头中的User-Agent
        String userAgent = request.getHeader("User-Agent");

        // 解析并校验token，判断用户是否登录
        Claims claims = parseToken(token);
        if (claims == null) {
            // 未通过，则直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 获取用户id
        String userId = claims.getId();

        boolean checkLoginRegister = checkLoginRegister(Long.valueOf(userId), userAgent, token);
        if (!checkLoginRegister) {
            // 未通过，则直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 获取key
        String key = LOGIN_USER_KEY + token;
        // 从Redis查询数据
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);

        // 判断用户登录信息保存是否为空
        if (userMap.isEmpty()) {
            // 获取Security存放用户key
            String securityUserKey = LOGIN_KEY + userId;
            // 删除用户权限信息
            stringRedisTemplate.delete(securityUserKey);
            // 直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 将map转换成Bean
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 将用户信息保存进线程之中
        UserHolder.saveUser(userDTO);
        // 刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 从redis中获取系统用户相关信息
        String result = stringRedisTemplate.opsForValue().get(LOGIN_KEY + userId);

        // 判断用户权限信息是否为空
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
        stringRedisTemplate.expire(LOGIN_KEY + userId, LOGIN_KEY_TTL, TimeUnit.MINUTES);

        // 用户访问新增
        addPV(userId);

        // 放行，让后面的过滤器执行
        filterChain.doFilter(request, response);
    }

    /**
     * 访问用户登记
     * @param userId 用户ID
     */
    private void addPV(String userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");
        String time = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);

        // 获取key
        String key = USER_PV_KEY + time;

        stringRedisTemplate.opsForHyperLogLog().add(key, userId);
    }

    /**
     * 登录注册验证 防止token盗用
     * @param userId 用户ID
     * @param userAgent userAgent请求头
     * @param jwt 登录令牌
     * @return boolean
     */
    private boolean checkLoginRegister(Long userId, String userAgent, String jwt) {
        //解析agent字符串
        UserAgent agent = UserAgent.parseUserAgentString(userAgent);
        //获取浏览器对象
        Browser browser = agent.getBrowser();
        //获取操作系统对象
        OperatingSystem operatingSystem = agent.getOperatingSystem();

        String systemName = operatingSystem.getName();// 操作系统
        String browserName = browser.getName(); // 登录浏览器
        String browserType = browser.getBrowserType().getName(); // 登录浏览器类型
        Version browserVersion = agent.getBrowserVersion();// 登录浏览器版本

        // 获取Key
        String key = LOGIN_REGISTER_KEY + userId;
        // 查询登录登记
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        // 判断是否为空
        if (map.isEmpty()) {
            return false;
        }
        // 获取token
        Object token = map.get("token");
        // 判断是否为空
        if (token == null) {
            return false;
        }
        // 判断token是否不一致
        if (!jwt.equals(token.toString())) {
            return false;
        }
        // 获取操作系统
        Object systemName1 = map.get("systemName");
        // 判断操作系统是否不一致
        if (systemName1 != null) {
            if (!systemName.equals(systemName1.toString())) {
                return false;
            }
        }
        // 获取浏览器
        Object browserName1 = map.get("browserName");
        // 判断浏览器是否不一致
        if (browserName1 != null) {
            if (!browserName.equals(browserName1.toString())) {
                return false;
            }
        }
        // 获取浏览器类型
        Object browserType1 = map.get("browserType");
        // 判断浏览器类型是否不一致
        if (browserType1 != null) {
            if (!browserType.equals(browserType1.toString())) {
                return false;
            }
        }
        if (browserVersion != null && browserVersion.getVersion() != null) {
            // 获取浏览器版本
            Object browserVersion1 = map.get("browserVersion");
            // 判断浏览器版本是否不一致
            if (browserVersion1 != null) {
                if (!browserVersion.getVersion().equals(browserVersion1.toString())) {
                    return false;
                }
            }
        }


        // 刷新时效
        stringRedisTemplate.expire(key, LOGIN_REGISTER_TTL, TimeUnit.MINUTES);

        return true;
    }

    /**
     * token解析并校验
     * @param token token令牌
     * @return 是否通过校验
     */
    private Claims parseToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        Claims claims = JwtUtils.parseJWT(token);
        if (claims == null) {
            return null;
        }
        // 获取jwt密钥发行方
        if (!claims.getIssuer().equals(JWT_ISSUER)) {
            return null;
        }
        // 获取jwt密钥主题
        if (!claims.getSubject().equals(JWT_SUBJECT)) {
            return null;
        }
        // 获取jwt受众群体
        if (!claims.getAudience().equals(JWT_AUDIENCE)) {
            return null;
        }

        return claims;
    }
}
