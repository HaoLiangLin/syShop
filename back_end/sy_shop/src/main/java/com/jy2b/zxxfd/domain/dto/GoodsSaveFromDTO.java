package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

// 新增商品DTO
@Data
public class GoodsSaveFromDTO {
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
}
