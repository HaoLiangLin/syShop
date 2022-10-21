package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

// 密码信息
@Data
public class PwdFormDTO {
    private String phone;
    private String oldPassword;
    private String newPassword;
}
