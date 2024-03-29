package com.jy2b.zxxfd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 林武泰
 * WebMvc配置类
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 设置允许cookie
                .allowCredentials(true)
                // 设置允许请求方式
                .allowedMethods("GET","POST","DELETE","PUT","OPTIONS")
                // 设置允许的header请求
                .allowedHeaders("*")
                // 设置跨域允许时间
                .maxAge(3600);
    }
}
