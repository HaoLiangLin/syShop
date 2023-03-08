package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class ForgetPasswordDTO {
    private String username; // 用户名
    private String phone; // 手机号
    private String code; // 验证码
    private String newPassword; // 新密码
}
