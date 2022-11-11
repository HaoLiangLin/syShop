package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class NoticeQueryDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private Long noticeCategoryId;

    private String timeSort; // 销量排序  升序:Asc、降序:Des
}
