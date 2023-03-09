package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
@TableName("tb_recharge_combo")
public class RechargeCombo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;

    @TableField("name")
    private String name;

    @TableField("price")
    private Double price;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("points")
    private Integer points;

    @TableField("discount")
    private Double discount;
}
