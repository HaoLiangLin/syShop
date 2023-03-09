package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class OrderItemDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderItemId; // 订单属性id

    private String goodsName; // 商品名称

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long gid; // 商品属性id
    private String color; // 颜色
    private String image; // 商品图片
    private String combo; // 套餐
    private String size; // 尺寸
    private String edition; // 版本
    private Double unitPrice; // 商品单价
    private Double price; // 商品总价

    private Integer quantity; // 下单数量

    private Integer isComment; // 是否已评价
}
