package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class CoerceLogoutDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId; // 用户ID
    private String token; // 登录令牌
}
