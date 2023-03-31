package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class NoticeQueryDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long noticeCategoryId;

    private String timeSort; // 时间排序  升序:Asc、降序:Des
}
