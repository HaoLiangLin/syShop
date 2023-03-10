package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 林武泰
 */
@Data
@TableName("tb_order")
public class Order {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("uid")
    private Long uid;

    @TableField("time")
    private Date time;

    @TableField("price")
    private Double price;

    @TableField("remarks")
    private String remarks;

    @TableField("name")
    private String name;

    @TableField("phone")
    private String phone;

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

    @TableField("isPay")
    private Integer isPay;

    @TableField("payment_methods")
    private Integer paymentMethods;

    @TableField("payment_time")
    private Date paymentTime;

    @TableField("logistics_status")
    private Integer logisticsStatus;

    @TableField("shipping_time")
    private Date shippingTime;

    @TableField("return_time")
    private Date returnTime;

    @TableField("status")
    private Integer status;

    @TableField("update_time")
    private Date updateTime;

    @TableField("isDel")
    private Integer isDel;
}
