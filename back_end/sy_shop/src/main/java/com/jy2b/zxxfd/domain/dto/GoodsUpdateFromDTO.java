package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class GoodsUpdateFromDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id; // 商品id

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long cid;

    private String images;

    private String province;

    private String city;

    private String district;

    private String address;

    private Double postage;

    private Integer recommend; // 推荐商品

    private Integer warrantyTime;

    private Integer refundTime;

    private Integer changerTime;

    private Integer status;
}
