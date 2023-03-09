package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 林武泰
 */
@Data
public class EvaluationCommentSaveDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long evaluationId; // 评价id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fid; // 评论父id

    private String content; // 评价内容
}
