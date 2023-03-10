package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class NoticeDTO {
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long cid;

    private String content;
}
