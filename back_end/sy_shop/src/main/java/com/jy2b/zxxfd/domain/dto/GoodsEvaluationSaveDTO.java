package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class GoodsEvaluationSaveDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderItemId; // 订单属性id

    private Integer stars; // 评价星级

    private String images; // 评价图片

    private String content; // 评价内容
}
