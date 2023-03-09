package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
// 钱包充值DTO
@Data
public class RechargeFromDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long rechargeComboId; // 充值套餐id

    private Double recharge; // 充值金额

    private Double consumption; // 消费金额
}
