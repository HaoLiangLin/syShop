package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class SaveUserDTO {
    private String username; // 用户名
    private String phone; // 手机号
    private String password; // 密码
    private Integer userType; // 用户类型
    private Integer status; // 用户状态
}
