package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 林武泰
 */
@Data
@TableName("tb_user_info")
public class UserInfo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
    @JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField("birthday")
    private Date birthday;
    @TableField("qq")
    private String qq;
    @TableField("level")
    private Integer level;
}
