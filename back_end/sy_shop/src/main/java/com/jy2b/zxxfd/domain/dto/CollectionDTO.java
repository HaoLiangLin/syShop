package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class CollectionDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long gid; // 商品id

    private String name; // 名字

    private String images; // 图片

    private Integer collection; // 收藏数

    private Double price; // 价格
}
