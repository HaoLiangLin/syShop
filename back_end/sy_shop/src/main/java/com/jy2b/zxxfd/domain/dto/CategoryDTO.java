package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * @author 林武泰
 */
@Data
public class CategoryDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String name;

    private String icon;

    private String remark;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fid;

    private List<CategoryDTO> children;
}
