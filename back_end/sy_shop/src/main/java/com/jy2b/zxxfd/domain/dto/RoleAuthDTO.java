package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * @author 林武泰
 */
@Data
public class RoleAuthDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> authIds;
}
