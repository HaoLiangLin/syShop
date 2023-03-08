package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class UpdateUserDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id; // 用户id
    private String username; // 用户名
    private String phone; // 手机号
    private Integer status; // 用户状态，0：正常，1：停用
}
