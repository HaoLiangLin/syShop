package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class GoodsCommentSaveDTO {
    private Long orderItemId; // 订单属性id

    private Integer stars; // 评价星级

    private String images; // 评价图片

    private String content; // 评价内容
}
