package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_role")
public class Role {
    @TableId("id")
    private Long id;
    @TableField("name")
    private String name;
    @TableField("perms")
    private String perms;
    @TableField("status")
    private Integer status;
    @TableField("remark")
    private String remark;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
}
