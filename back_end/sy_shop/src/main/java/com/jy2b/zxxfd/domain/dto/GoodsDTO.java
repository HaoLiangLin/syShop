package com.jy2b.zxxfd.domain.dto;

import com.jy2b.zxxfd.domain.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 林武泰
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDTO {
    private Goods goods;

    private Double price;

    private Double discount;
}
