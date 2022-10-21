package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private Long id; // 订单号

    private Long uid; // 下单用户id

    private Integer isPay; // 是否付款

    private Integer logisticsStatus; // 发货状态

    private Integer status; // 订单状态

    private String timeSort; // 下单时间排序  升序:Asc、降序:Des

    private String priceSort; // 价格排序  升序:Asc、降序:Des
}
