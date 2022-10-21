package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_order_item")
public class OrderItem {
    @TableId("id")
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("gid")
    private Long gid;

    @TableField("quantity")
    private Integer quantity;

    @TableField("unit_price")
    private Double unitPrice;

    @TableField("price")
    private Double price;
}
