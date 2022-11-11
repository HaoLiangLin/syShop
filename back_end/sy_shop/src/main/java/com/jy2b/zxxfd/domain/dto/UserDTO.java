package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

// 返回用户信息
@Data
public class UserDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String username;
    private String phone;
    private String icon;
    private String nickname;
    private Integer userType;
    private Date createTime;
    private Date updateTime;
}
