package com.jy2b.zxxfd.utils;

import com.jy2b.zxxfd.contants.SystemConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.util.Date;

/**
 * @author 林武泰
 * JWT工具类
 */
public class JwtUtils {
    // 设置字符串密钥
    private static final String JWT_KEY =
            "1664812800000_GDSlDlZyJsXy_20JiYing2_2022_10_04_0000000_zxXfd_20syShop_PM200114216_WuTaiLin_1234567890";

    /**
     * 生成jwt
     * @param userId 用户id
     * @return jwt
     */
    public static String generateJwt(Long userId) {
        // 生成密钥
        SecretKey secretKey = generateKey();

        String id = String.valueOf(userId);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(id) // 设置id
                .setIssuer(SystemConstants.JWT_ISSUER) // 设置密钥发行方
                .setSubject(SystemConstants.JWT_SUBJECT) // 设置密钥主题
                .setAudience(SystemConstants.JWT_AUDIENCE) // 设置密钥受众群体
                .setIssuedAt(new Date()) // 设置签发时间
                .signWith(secretKey, SignatureAlgorithm.HS512);// 指定密钥签名

        // 返回最后生成的jwt
        return jwtBuilder.compact();
    }

    /**
     * 解析jwt
     * @param jwt jwt
     * @return Claims
     */
    public static Claims parseJWT(String jwt) {
        // 生成密钥
        SecretKey secretKey = generateKey();

        // 解析jwt并返回
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 生成密钥
     * @return SecretKey
     */
    public static SecretKey generateKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(JWT_KEY));
    }
}
