package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class GoodsItemSaveFromDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long gid; // 商品id

    private String color; // 颜色

    private String combo; // 套餐

    private String size; // 尺寸

    private String edition; // 版本

    private Double price; // 价格

    private Double discount; // 则扣

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long stock; // 库存

    private Integer status;

    private String remark; // 备注
}
