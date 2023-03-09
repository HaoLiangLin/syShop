package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 林武泰
 */
@Data
@TableName("tb_role")
public class Role {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id; // 权限id
    @TableField("name")
    private String name; // 权限名称
    @TableField("perms")
    private String perms; // 权限标识
    @TableField("status")
    private Integer status; // 权限状态
    @TableField("label")
    private String label; // 权限标签
    @TableField("remark")
    private String remark; // 权限备注
    @TableField("create_time")
    private Date createTime; // 创建时间
    @TableField("update_time")
    private Date updateTime; // 修改时间
}
