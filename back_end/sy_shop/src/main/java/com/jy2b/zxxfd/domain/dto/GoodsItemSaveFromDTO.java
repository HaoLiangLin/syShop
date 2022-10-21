package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class GoodsItemSaveFromDTO {
    private String gid; // 商品id

    private String color; // 颜色

    private String combo; // 套餐

    private String size; // 尺寸

    private String edition; // 版本

    private Double price; // 价格

    private Double discount; // 则扣

    private Long stock; // 库存

    private String icon; // 图片



    private String remark; // 备注
}
