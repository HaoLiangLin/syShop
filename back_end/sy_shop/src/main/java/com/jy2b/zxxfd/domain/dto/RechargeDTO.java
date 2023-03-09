package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class RechargeDTO {
    private Integer chargeType; // 充值类型 0:任意充值，1:套餐充值
    private Long rechargeComboId; // 充值套餐ID
    private Double rechargeAmount; // 充值金额
}
