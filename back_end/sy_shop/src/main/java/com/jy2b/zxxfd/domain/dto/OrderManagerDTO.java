package com.jy2b.zxxfd.domain.dto;

import com.jy2b.zxxfd.domain.Order;
import com.jy2b.zxxfd.domain.OrderItem;
import com.jy2b.zxxfd.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class OrderManagerDTO {
    private Order order;
    private User user;
    private List<OrderItem> orderItemList;
}
