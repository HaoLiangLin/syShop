package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username; // 用户名
    private String phone; // 手机号
    private String password; // 密码
    private String code; // 验证码
}
