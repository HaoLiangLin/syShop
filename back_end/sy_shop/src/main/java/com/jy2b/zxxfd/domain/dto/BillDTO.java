package com.jy2b.zxxfd.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {
    private String time; // 时间

    private String type; // 类型：充值/消费

    private String amount; // 金额
}
