package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_recharge_combo")
public class RechargeCombo {
    @TableId("id")
    private Long id;

    @TableField("name")
    private String name;

    @TableField("price")
    private Double price;

    @TableField("points")
    private Long points;

    @TableField("discount")
    private Double discount;
}
