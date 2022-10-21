package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class ShoppingCartDTO {
    private Long id; // 购物车id

    private String name; // 商品名称

    private Long gid; // 商品属性id

    private String icon; // 图片

    private String color; // 颜色

    private String size; // 尺寸

    private String combo; // 套餐

    private String edition; // 版本

    private Integer quantity; // 数量

    private Double unitPrice; // 单价

    private Double postage; // 运费

    private Double price; // 价格
}
