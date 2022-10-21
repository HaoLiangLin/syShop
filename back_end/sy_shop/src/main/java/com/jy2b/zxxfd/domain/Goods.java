package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_goods")
public class Goods {
    @TableId("id")
    private Long id;

    @TableField("name")
    private String name;

    @TableField("cid")
    private Long cid;

    @TableField("images")
    private String images;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("district")
    private String district;

    @TableField("address")
    private String address;

    @TableField("postage")
    private Double postage;

    @TableField("monthSale")
    private Long monthSale;

    @TableField("warranty_time")
    private Integer warrantyTime;

    @TableField("refund_time")
    private Integer refundTime;

    @TableField("changer_time")
    private Integer changerTime;

    @TableField("status")
    private Integer status;

    @TableField("shelves_time")
    private Date shelvesTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic
    @TableField("isDel")
    private Integer isDel;
}
