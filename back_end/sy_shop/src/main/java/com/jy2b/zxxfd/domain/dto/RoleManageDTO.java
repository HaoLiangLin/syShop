package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class RoleManageDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id; // 角色id
    private String name; // 角色名称
    private String perms; // 角色标识
    private Integer status; // 角色状态
    private String label; // 角色标签
}
