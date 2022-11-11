package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class RechargeComboSaveDTO {
    private String name;

    private Double price;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long points;

    private Double discount;
}
