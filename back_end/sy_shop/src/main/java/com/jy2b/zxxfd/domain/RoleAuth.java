package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
@TableName("tb_role_auth")
public class RoleAuth {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("role_id")
    private Long roleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("auth_id")
    private Long authId;
}
