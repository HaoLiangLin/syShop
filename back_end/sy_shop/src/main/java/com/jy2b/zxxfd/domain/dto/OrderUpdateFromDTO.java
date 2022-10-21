package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class OrderUpdateFromDTO {
    private Long id; // 订单号
    private String name; // 收件人
    private String phone; // 联系电话
    private String province; // 省份
    private String city; // 城市
    private String district; // 区/县
    private String address; // 详细收货地址
    private String remarks; // 订单备注

    private Integer logisticsStatus; // 物流状态

    private Integer status; // 订单状态
}
