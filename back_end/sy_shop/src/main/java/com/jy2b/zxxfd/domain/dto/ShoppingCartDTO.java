package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class ShoppingCartDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id; // 购物车id

    private String name; // 商品名称

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsId; // 商品id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsItemId; // 商品属性id

    private String icon; // 图片

    private String color; // 颜色

    private String size; // 尺寸

    private String combo; // 套餐

    private String edition; // 版本

    private Integer quantity; // 数量

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long stock; // 库存

    private Double unitPrice; // 单价

    private Double postage; // 运费

    private Double price; // 价格
}
