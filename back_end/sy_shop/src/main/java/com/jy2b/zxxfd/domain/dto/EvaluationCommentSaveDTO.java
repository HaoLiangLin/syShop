package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class EvaluationCommentSaveDTO {
    private Long evaluationId; // 评价id

    private Long fid; // 评论父id

    private String content; // 评价内容
}
