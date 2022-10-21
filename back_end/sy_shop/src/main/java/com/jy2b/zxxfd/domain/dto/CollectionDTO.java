package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class CollectionDTO {
    private Long gid; // 商品id

    private String name; // 名字

    private String images; // 图片

    private Integer collection; // 收藏数

    private Double price; // 价格
}
