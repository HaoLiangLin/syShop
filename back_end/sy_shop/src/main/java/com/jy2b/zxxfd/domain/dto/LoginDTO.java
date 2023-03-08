package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username; // 用户名
    private String password; // 密码
    private String phone; // 手机号
    private String code; // 验证码
    private Integer loginType; // 0 账号登录、1 手机号验证码登录
}
