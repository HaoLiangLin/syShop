package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 林武泰
 */
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
    private Date usernameUpdateTime; // 用户名修改时间
    private Date lastLogin; // 最后登录时间
}
