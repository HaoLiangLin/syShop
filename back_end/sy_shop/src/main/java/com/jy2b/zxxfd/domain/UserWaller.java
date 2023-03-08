package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
@TableName("tb_user_account")
public class UserWaller {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;
    @TableField("balance")
    private Double balance;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("points")
    private Long points;
    @TableField("recharge")
    private Double recharge;
    @TableField("spending")
    private Double spending;
}
