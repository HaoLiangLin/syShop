package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.Date;

// 返回用户信息
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String phone;
    private String icon;
    private String nickname;
    private Integer userType;
    private Date updateTime;
}
