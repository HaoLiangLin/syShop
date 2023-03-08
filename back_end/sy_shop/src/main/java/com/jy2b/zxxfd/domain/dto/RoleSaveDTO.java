package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class RoleSaveDTO {
    private String name; // 角色名称
    private String perms; // 角色标识
    private String label; // 角色标签
    private String remark; // 角色备注
}
