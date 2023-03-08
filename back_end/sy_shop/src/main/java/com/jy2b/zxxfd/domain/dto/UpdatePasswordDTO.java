package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String oldPassword; // 旧密码
    private String newPassword; // 新密码
}
