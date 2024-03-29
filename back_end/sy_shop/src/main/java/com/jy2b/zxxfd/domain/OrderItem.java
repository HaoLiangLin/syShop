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
@TableName("tb_order_item")
public class OrderItem {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("order_id")
    private Long orderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("goodsItem_id")
    private Long goodsItemId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("unit")
    private Integer unit; // 货币单位 0：货币，1：积分

    @TableField("unit_price")
    private Double unitPrice;

    @TableField("price")
    private Double price;

    @TableField("goods_name")
    private String goodsName;

    @TableField("goodsItem_icon")
    private String goodsItemIcon;

    @TableField("goodsItem_color")
    private String goodsItemColor;

    @TableField("goodsItem_size")
    private String goodsItemSize;

    @TableField("goodsItem_combo")
    private String goodsItemCombo;

    @TableField("goodsItem_edition")
    private String goodsItemEdition;

    @TableField("isComment")
    private Integer isComment;
}
