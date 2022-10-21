package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.List;

// 用户查询信息
@Data
public class UserQueryFromDTO {
    private Long id;

    private String username;

    private String phone;

    private Integer status;

    private List<Long> ids;
}
