package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderSaveFromDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long aid; // 收货地址id

    private ArrayList<OrderSaveDTO> items; // 订单商品详细

    private String remarks; // 订单备注
}
