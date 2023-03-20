package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class OrderStatusUpdateDTO {
    private Integer logisticsStatus; // 物流状态

    private Integer status; // 订单状态
}
