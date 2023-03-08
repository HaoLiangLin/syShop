package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class AuthSaveDTO {
    private String name; // 权限名称
    private String perms; // 权限标识
    private Integer type; // 权限类型
    private String label; // 权限标签
    private String remark; // 权限备注
}
