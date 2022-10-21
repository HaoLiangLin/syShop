package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_user_info")
public class UserInfo {
    @TableId("id")
    private Long id;
    @TableField("full_name")
    private String fullName;
    @TableField("gender")
    private String gender;
    @TableField("age")
    private Integer age;
    @TableField("email")
    private String email;
    @TableField("birthday")
    private Date birthday;
    @TableField("qq")
    private String qq;
    @TableField("level")
    private Integer level;
}
