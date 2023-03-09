package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class AuthUpdateDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id; // 权限id
    private String name; // 权限名称
    private String perms; // 权限标识
    private Integer status; // 权限状态
    private Integer type; // 权限类型
    private String label; // 权限标签
    private String remark; // 权限备注
}
