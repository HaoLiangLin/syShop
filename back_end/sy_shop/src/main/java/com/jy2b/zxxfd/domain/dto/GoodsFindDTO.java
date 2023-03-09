package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 林武泰
 */
@Data
public class GoodsFindDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long cid;

    private String cname; // 分类名称

    private String images;

    private String province;

    private String city;

    private String district;

    private String address;

    private Double postage;

    private Integer recommend;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sale;

    private Integer warrantyTime;

    private Integer refundTime;

    private Integer changerTime;

    private Integer status;

    private Date shelvesTime;

    private Date updateTime;
}
