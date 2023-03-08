package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("tb_role_auth")
@NoArgsConstructor
@AllArgsConstructor
public class RoleAuth {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("role_id")
    private Long roleId;  // 角色id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("auth_id")
    private Long authId; // 权限id
}
