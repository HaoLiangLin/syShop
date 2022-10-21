package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderSaveFromDTO {
    private Long aid; // 收货地址id

    private ArrayList<OrderSaveDTO> items; // 订单商品详细

    private String remarks; // 订单备注
}
