package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class GoodsQueryFromDTO {
    private String name; // 商品名称
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long cid; // 分类id
    private Integer recommend; // 是否推荐
    private Integer isNew; // 是否新品
    private String saleSort; // 销量排序  升序:Asc、降序:Des
    private String priceSort; // 价格排序  升序:Asc、降序:Des
}
