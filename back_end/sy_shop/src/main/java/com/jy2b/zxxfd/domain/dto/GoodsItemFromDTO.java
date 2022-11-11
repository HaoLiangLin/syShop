package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class GoodsItemFromDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long gid;
    private String color;
    private String size;
    private String combo;
    private String edition;
}
