package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class RechargeComboManageDTO {
    private String name; // 套餐名称
    private Double price; // 充值金额
    private Integer points; // 赠送积分
    private Double discount; // 折扣
}
