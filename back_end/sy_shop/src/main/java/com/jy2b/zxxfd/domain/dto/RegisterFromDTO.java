package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

// 注册表单信息
@Data
public class RegisterFromDTO {
    private String username;

    private String phone;

    private String code;

    private String password;
}
