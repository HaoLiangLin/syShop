package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_user")
public class User {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;
    @TableField("username")
    private String username;
    @TableField("phone")
    private String phone;
    @TableField("password")
    private String password;
    @TableField("nickname")
    private String nickname;
    @TableField("icon")
    private String icon;
    @TableField("user_type")
    private Integer userType;
    @TableField("isUpdate")
    private Integer isUpdate;
    @TableField("isPassword")
    private Integer isPassword;
    @TableField("status")
    private Integer status;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("username_update_time")
    private Date usernameUpdateTime; // 用户名修改时间
    @TableField("last_login")
    private Date lastLogin; // 最后登录时间
    @TableLogic
    @TableField("isDel")
    private Integer isDel;
}
