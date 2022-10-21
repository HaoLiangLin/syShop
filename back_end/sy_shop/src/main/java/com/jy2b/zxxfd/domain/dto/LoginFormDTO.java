package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

// 登录表单信息
@Data
public class LoginFormDTO {
    // 0 账号登录、1 手机号验证码登录
    private Integer loginType;
    private String username;
    private String password;
    private String phone;
    private String code;
}
