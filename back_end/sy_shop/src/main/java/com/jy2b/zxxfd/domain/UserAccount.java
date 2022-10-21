package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_user_account")
public class UserAccount {
    @TableId("id")
    private Long id;
    @TableField("balance")
    private Double balance;
    @TableField("points")
    private Long points;
    @TableField("recharge")
    private Double recharge;
    @TableField("spending")
    private Double spending;
}
