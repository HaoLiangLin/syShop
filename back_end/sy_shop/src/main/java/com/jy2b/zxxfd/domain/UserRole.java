package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 林武泰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user_role")
public class UserRole {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("user_id")
    private Long userId; // 用户id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("role_id")
    private Long roleId; // 角色id
}
