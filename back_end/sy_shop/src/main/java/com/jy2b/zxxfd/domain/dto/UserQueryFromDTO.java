package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

// 用户查询信息
@Data
public class UserQueryFromDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String username;

    private String phone;

    private Integer status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> ids;
}
