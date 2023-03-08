package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class UserManagerDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id; // 用户Id
    private String username; // 用户名
    private String phone; // 手机号
    private Integer userType; // 用户类型，0：管理员，1：用户
    private Integer status; // 账号状态，0：正常，1：停用
    private Integer createTimeSort; // 创建时间排序，0：降序，1：升序
    private Integer lastLoginSort; // 最后登录时间排序，0：降序，1：升序
}
