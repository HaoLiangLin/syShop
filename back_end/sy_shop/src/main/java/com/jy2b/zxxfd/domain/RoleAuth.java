package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_role_auth")
public class RoleAuth {

    @TableId("role_id")
    private Long roleId;

    @TableId("auth_id")
    private Long authId;
}
