package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class RechargeComboSaveDTO {
    private String name;

    private Double price;

    private Long points;

    private Double discount;
}
