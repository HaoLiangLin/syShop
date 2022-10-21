package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

// 钱包充值DTO
@Data
public class RechargeFromDTO {
    private Long rechargeComboId; // 充值套餐id

    private Double recharge; // 充值金额

    private Double consumption; // 消费金额
}
