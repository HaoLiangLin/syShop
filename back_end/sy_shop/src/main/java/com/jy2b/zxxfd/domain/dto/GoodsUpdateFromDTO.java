package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class GoodsUpdateFromDTO {
    private Long id; // 商品id

    private String name;

    private Long cid;

    private String images;

    private String province;

    private String city;

    private String district;

    private String address;

    private Double postage;

    private Integer warrantyTime;

    private Integer refundTime;

    private Integer changerTime;

    private Integer status;
}
