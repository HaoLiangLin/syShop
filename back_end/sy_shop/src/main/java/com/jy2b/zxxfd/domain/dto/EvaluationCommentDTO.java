package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;

@Data
public class EvaluationCommentDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String userIcon;

    private String nickname;

    private String time;

    private String content;

    private Integer liked;

    private Integer isLiked;

    private Integer isAuthor;

    private Integer isMe;

    private ArrayList<EvaluationCommentDTO> childComments;
}
