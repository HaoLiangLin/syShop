package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderDTO {
    private Long orderId; // 订单号
    private String name; // 收件人
    private String phone; // 联系电话
    private String address; // 收货地址
    private Double postage; // 商品邮费
    private Double price; // 订单总额
    private String isPay; // 是否支付
    private String paymentMethods; // 支付方式
    private String logisticsStatus; // 物流状态
    private String shippingTime; // 发货时间
    private String status; // 订单状态
    private String time; // 下单时间
    private String remarks; // 订单备注

    private ArrayList<OrderItemDTO> orderItems; // 订单属性详情
}
