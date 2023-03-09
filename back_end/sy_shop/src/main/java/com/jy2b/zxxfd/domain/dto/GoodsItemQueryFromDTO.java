package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class GoodsItemQueryFromDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long gid; // 商品id

    private String color; // 颜色

    private String combo; // 套餐

    private String size; // 尺寸

    private String edition; // 版本

    private String priceSort; // 价格排序  升序:Asc、降序:Des

    private String stockSort; // 库存排序  升序:Asc、降序:Des

    private String salesSort; // 销量排序  升序:Asc、降序:Des

    private Integer status; // 状态
}
