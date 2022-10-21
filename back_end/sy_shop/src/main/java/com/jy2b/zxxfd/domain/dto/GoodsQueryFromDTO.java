package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class GoodsQueryFromDTO {
    private String name; // 商品名称
    private Long cid; // 分类id
    private String saleSort; // 销量排序  升序:Asc、降序:Des
}
