package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class GoodsCategoryDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fid;

    private String remark;
}
